<script context="module" lang="ts">
  /** @type {import('@sveltejs/kit').Load} */
  export async function load({ params, fetch, session, stuff }) {
    const group: string = params.group
    const page: any = params.page
    const main_res: Response = await fetch(`http://localhost:8080/v1/messages/${group}/page/${page}`);
    const main_res_json: any = await main_res.json();

    // TODO: handle timeouts & rejects

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

    const sub_res: Response = await fetch(`http://localhost:8080/v1/messages/${group}/pages`)

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
  interface Message {
    id: number;
    authorId: number;
    alias: string;
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

  page = Number(page);
  pages = Number(pages);

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
    <div id="messages">
      {#each page_data.messages as message (message.id)}
      <div class="py-2 border-zinc-200 dark:border-zinc-800 border-b-2 last:border-b-0">
        <p class="font-extrabold my-1"><a href="/user/{message.authorId}" class="no-url">{message.alias}</a></p>
        <p>
          <span class="font-semibold">Subject:</span>
          {#if message.subject !== null}
            {message.subject}
          {/if}
        </p>
        <div class="my-2 p-1 pl-3 border-l-4 border-zinc-300 dark:border-zinc-700">
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