<script context="module" lang="ts">
  export const router = false;
  /** @type {import('@sveltejs/kit').Load} */
  export async function load({ url, params, fetch }) {
    const group: string = params.group;
    const query: string = url.search;

    if (query === null || query.length === 0) {
      return {
        props: {
          "group": group,
          "results": {
            error: 'No search parameters were provided',
            getError() { return this.error; }
          }
        }
      }
    }

    let res: Response;
    let res_json: any;
    try {
      res = await fetch(`https://yahoo.qixils.dev/v1/messages/${group}/search${query}`);
      res_json = await res.json();
    } catch (error) {
      return {
        props: {
          "group": group,
          "results": {
            "error": "The API server failed to respond and may be undergoing maintenance. The provided error message was <b>" + error + "</b>",
            getError() { return this.error; }
          }
        }
      }
    }

    if (res_json.results === undefined && res_json.error === undefined) {
      return {
        props: {
          "group": group,
          "results": {
            error: 'The API server threw an unexpected exception (' + res.status + ')',
            getError() { return this.error; }
          }
        }
      }
    }

    return {
      props: {
        "group": group,
        "query": query,
        "results": res_json
      }
    }
  }
</script>

<script lang="ts">
  import { browser } from '$app/env';
  import Error from '$lib/Error.svelte';
  import Messages from '$lib/Messages.svelte';
  import type ResultData from '$lib/Constants';
  export let group: string;
  export let query: string;
  export let results: ResultData;
  let more_messages = results.isFinalPage;
  let messages = results.results;
  let offset = 0;

  function load_more() {
    offset += 50;
    fetch(`https://yahoo.qixils.dev/v1/messages/${group}/search${query}&offset=${offset}`)
    .then(resp => resp.json())
    .then((resp: ResultData) => {
      if (resp.error !== undefined || resp.results.length === 0) {
        more_messages = false;
      } else {
        messages = messages.concat(resp.results);
      }
    });
  }

  const observer = browser ? new IntersectionObserver(load_more, {}) : null;

  if (browser && results.error === undefined) {
    window.addEventListener("sveltekit:start", event => {
      const element = document.querySelector("#beacon");
      if (element !== null) {
        observer.observe(element);
      }
    });
  }
</script>

<svelte:head>
  <title>Yahoo! Groups Viewer ∙ Advanced Search Results</title>
	<meta property="og:title" content="Yahoo! Groups Viewer ∙ Advanced Search Results">
  <meta name="description" content="Search results for a query in the {group} group on the defunct website Yahoo! Groups.">
  <meta property="og:description" content="Search results for a query in the {group} group on the defunct website Yahoo! Groups.">
  <meta name="robots" content="noindex">
  <meta property="og:url" content="https://yahoo.qixils.dev/search/{group}{query}">
  <link rel="canonical" href="https://yahoo.qixils.dev/search/{group}{query}">
</svelte:head>

<header class="text-center">
  <h1 class="yahoo-header"><a href="/" class="no-url">Yahoo! Groups Viewer</a></h1>
  <h2 class="text-lg">Advanced search results for {group}</h2>
  <hr class="mt-4 mb-1 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
  {#if results.error !== undefined}
    <Error error_text="An error occurred while trying to load this page: {results.error}" />
  {:else}
    <Messages messages={messages}/>
    {#if !more_messages}
      <div class="py-2.5 border-zinc-200 dark:border-zinc-800 border-t-2" id="beacon">
        <div class="loading loading-xs"></div>
        <div class="loading loading-sm"></div>
        <div class="my-2 p-1 pl-3 border-l-4 border-zinc-300 dark:border-zinc-700">
          <div class="loading loading-xl loading-first"></div>
          <div class="loading loading-lg"></div>
          <div class="loading loading-md"></div>
          <div class="loading loading-md loading-last"></div>
        </div>
      </div>
    {/if}
  {/if}
</main>

<style lang="postcss">
  .loading {
    @apply h-4 my-3 w-full animate-pulse rounded-sm;
    background-color: rgba(131, 131, 141, 0.4);
  }
  .loading-first {@apply mt-1.5;}
  .loading-last {@apply mb-1.5;}
  .loading-xs {max-width: 8em;}
  .loading-sm {max-width: 10em;}
  .loading-md {max-width: 20em;}
  .loading-lg {max-width: 30em;}
  .loading-xl {max-width: 40em;}
</style>
