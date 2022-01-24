<script context="module" lang="ts">
  export const router = false;
  /** @type {import('@sveltejs/kit').Load} */
  export async function load({ params, fetch }) {
    const group: string = params.group;
    const page: any = params.page;
    let main_res: Response;
    let main_res_json: any;
    try {
      main_res = await fetch(`https://yahoo.qixils.dev/v1/messages/${group}/page/${page}`);
      main_res_json = await main_res.json();
    } catch (error) {
      return {
        props: {
          "group": group,
          "page": [page],
          "pages": 0,
          "page_data": {
            "error": "The API server failed to respond and may be undergoing maintenance. The provided error message was <b>" + error + "</b>",
            getError() { return this.error; }
          }
        }
      }
    }

    if (main_res_json.messages === undefined && main_res_json.error === undefined) {
      return {
        props: {
          "group": group,
          "page": [page],
          "pages": 0,
          "page_data": {
            error: 'The API server threw an unexpected exception (' + main_res.status + ')',
            getError() { return this.error; }
          }
        }
      }
    }

    const sub_res: Response = await fetch(`https://yahoo.qixils.dev/v1/messages/${group}/pages`)

    return {
      props: {
        "group": group,
        "page": page,
        "pages": (await sub_res.json()).pages,
        "page_data": main_res_json
      }
    }
  }
</script>

<script lang="ts">
  import type Page from '$lib/Constants';
  import Pagination from '$lib/Pagination.svelte';
  import Error from '$lib/Error.svelte';
  import Messages from '$lib/Messages.svelte';

  export let group: string;
  export let page: number;
  export let pages: number;
  export let page_data: Page;

  page = Number(page);
  pages = Number(pages);
</script>

<svelte:head>
  <title>Yahoo! Groups Viewer ∙ Page {page}/{pages} of {group}</title>
  <meta name="description" content="An archive of emails sent to the {group} group on the defunct website Yahoo! Groups.">
  <meta property="og:description" content="An archive of emails sent to the {group} group on the defunct website Yahoo! Groups.">
  <meta name="robots" content="noindex">
  <meta property="og:url" content="https://yahoo.qixils.dev/group/{group}/{page}">
  <link rel="canonical" href="https://yahoo.qixils.dev/group/{group}/{page}">
</svelte:head>

<header class="text-center">
  <h1 class="yahoo-header"><a href="/" class="no-url">Yahoo! Groups Viewer</a></h1>
  <h2 class="text-lg">Group: {group} ∙ Page {page} of {pages}</h2>
  <hr class="mt-4 mb-1 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
  {#if page_data.error !== undefined}
    <Error error_text="An error occurred while trying to load this page: {page_data.error}" />
  {:else}
    <Messages messages={page_data.messages}/>
    <Pagination current={page} end={pages}/>
  {/if}
</main>
