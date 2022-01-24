<script context="module" lang="ts">
	/** @type {import('@sveltejs/kit').ErrorLoad} */
	export function load({ error, status }) {
		return {
			props: {
				"status": status,
                "message": error.message
			}
		};
	}
</script>

<script lang="ts">
    import Error from "$lib/Error.svelte";
	export let status: number;
    export let message: string;
</script>

<svelte:head>
    <title>Yahoo! Groups Viewer ∙ {status}</title>
</svelte:head>

<header class="text-center">
    <h1 class="yahoo-header"><a href="/" class="no-url">Yahoo! Groups Viewer</a></h1>
    <h2 class="text-lg">An unknown exception has occurred</h2>
    <hr class="mt-4 mb-1 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
    <Error error_text="The webpage you were trying to access has failed to load. The provided error was <b>{status} {message}</b>" />
</main>