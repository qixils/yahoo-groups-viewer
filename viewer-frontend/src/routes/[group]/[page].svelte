<!--suppress TypeScriptUnresolvedVariable-->
<!--(IntelliJ is for some reason not parsing the tsconfig.json file)-->
<script context="module">
  /** @type {import('@sveltejs/kit').Load} */
  export async function load({ params, fetch, session, stuff }) {
    const group = params.group
    const page = params.page
    const main_res = await fetch(`http://localhost:8080/messages/${group}/page/${page}`);
    const sub_res = await fetch(`http://localhost:8080/messages/${group}/pages`)

    if (false) { // TODO: handle timeouts
      return {
        props: {
          "group": group,
          "page": [page],
          "pages": 0,
          "page_data": null // TODO: create fake object with timeout error
        }
      }
    }

    return {
      props: {
        "group": group,
        "page": page,
        "pages": (await sub_res.json()).pages,
        "page_data": await main_res.json()
      }
    }
  }
</script>

<script lang="ts">
  interface User {
    id: number;
    userName: string | null;
    realName: string;
    displayName: string;
  }

  interface Message {
    id: number;
    user: User;
    postDate: number;
    subject: string | null;
    body: string;
    nextInTime: number;
  }

  interface Page {
    messages: Message[];
    previousPageIndex: number | null;
    nextPageIndex: number | null;
    error?: string;
  }

  export let group: string;
  export let page: number;
  export let pages: number;
  export let page_data: Page;

  import Pagination from '$lib/Pagination.svelte';
  import Error from '$lib/Error.svelte';

  function split(body: string): string[] {
    return body.split("\n\n");
  }

  function filterHTML(body: string): string {
    return body; // TODO basic HTML filtering (remove styles and tags besides <a> and <table>-related stuff)
  }
</script>

<svelte:head>
  <title>Yahoo! Groups Viewer ∙ Page {page}/{pages} of {group}</title>
  <meta name="description" content="An archive of emails sent to the {group} group on the defunct website Yahoo! Groups.">
  <meta property="og:description" content="An archive of emails sent to the {group} group on the defunct website Yahoo! Groups.">
  <meta name="robots" content="noindex">
</svelte:head>

<header class="text-center">
  <h1 class="yahoo-header"><a href="/">Yahoo! Groups Viewer</a></h1>
  <h2 class="text-lg">Group: {group} ∙ Page {page} of {pages}</h2>
  <hr class="mt-4 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
  {#if page_data.error !== undefined}
    <Error error_text="An error occurred while trying to load this page: {page_data.error}" />
  {:else}
    <div id="messages">
      {#each page_data.messages as message (message.id)}
      <div class="py-6 border-zinc-200 dark:border-zinc-800 border-b-2 last:border-b-0">
        <p>
          <span class="font-extrabold">{message.user.realName}</span>
          {#if message.user.userName !== null && message.user.userName !== message.user.realName}
            <span class="text-zinc-500 dark:text-zinc-400">{message.user.userName}</span>
          {/if}
        </p>
        <p>
          <span class="font-semibold">Subject:</span>
          {#if message.subject !== null}
            {message.subject}
          {/if}
        </p>
        <div class="mt-2">
          {#each split(filterHTML(message.body)) as line}
            <p>{@html line}</p>
          {/each}
        </div>
      </div>
    {/each}
    </div>

    <Pagination current={page} end={pages}/>
  {/if}
</main>