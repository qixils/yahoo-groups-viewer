<!--suppress TypeScriptUnresolvedVariable-->
<!--(IntelliJ is for some reason not parsing the tsconfig.json file)-->
<script context="module">
    /** @type {import('@sveltejs/kit').Load} */
    export async function load({ params, fetch, session, stuff }) {
        const group = params.group
        const page = params.page
        const main_res = await fetch(`http://localhost:8080/messages/${group}/page/${page}`);
        const sub_res = await fetch(`http://localhost:8080/messages/${group}/pages`)

        // TODO: handle timeouts

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
        userName?: string;
        realName: string;
        displayName: string;
    }

    interface Message {
        id: number;
        user: User;
        postDate: number;
        subject?: string;
        body: string;
        nextInTime: number;
    }

    interface Page {
        messages: Message[];
        previousPageIndex?: number;
        nextPageIndex?: number;
    }

    export let group: string
    export let page: number
    export let pages: number
    export let page_data: Page
    import Pagination from '$lib/Pagination.svelte'

    function split(body: string): string[] {
        return body.split("\n\n")
    }
</script>

<svelte:head>
    <title>Yahoo! Groups Viewer ∙ {group}, page {page}</title>
</svelte:head>

<header class="text-center">
    <h1 class="yahoo text-3xl"><a href="/">Yahoo! Groups Viewer</a></h1>
    <h2 class="text-xl">Group: {group} ∙ Page {page} of {pages}</h2>
    <hr class="my-3 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
    {#each page_data.messages as message (message.id)}
        <div class="py-6 border-zinc-200 dark:border-zinc-800 border-b-2 last:border-b-0">
            <p>
                <span class="font-bold">{message.user.realName}</span>
                {#if message.user.userName !== null && message.user.userName !== message.user.realName}
                    <span class="text-zinc-500 dark:text-zinc-400">{message.user.userName}</span>
                {/if}
            </p>
            <p>
                <span class="font-medium">Subject:</span>
                {#if message.subject !== null}
                    {message.subject}
                {/if}
            </p>
            <div class="mt-2">
                {#each split(message.body) as line}
                    <p>{line}</p>
                {/each}
            </div>
        </div>
    {/each}

    
    <!-- TODO: page does not properly load when clicking nav buttons; may just be due to dev environment? -->
    <Pagination current={page} end={pages}/>
</main>