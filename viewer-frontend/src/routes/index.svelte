<script context="module" lang="ts">
	export const prerender = true;
</script>

<script lang="ts">
	interface GroupData {
		groups: string[];
	}
	let groups_promise: Promise<GroupData> = fetch("https://yahoo.qixils.dev/v1/groups").then(response => response.json())

    import Error from "$lib/Error.svelte";
	let selected: string;
</script>

<svelte:head>
	<title>Yahoo! Groups Viewer</title>
	<meta name="description" content="A hub to view archives of emails sent to groups on the defunct website Yahoo! Groups.">
	<meta property="og:description" content="A hub to view archives of emails sent to groups on the defunct website Yahoo! Groups.">
</svelte:head>

<main class="text-center max-w-2xl mx-auto">
	<h2 class="yahoo-header mb-1 no-url">Yahoo! Groups Viewer</h2>
	<div class="md:text-lg">
		<p>
			A tool to view archives of emails from the now-defunct
			<span class="yahoo">Yahoo! Groups</span>.
		</p>
		<p>To begin, simply select a group to view from the dropdown list below.</p>
	</div>

	<div class="my-4">
		{#await groups_promise}
			<p>Loading groups, please wait...</p>
		{:then group_data}
			<!-- Form theming adopted from https://flowbite.com/docs/components/forms/ -->
			<select bind:value={selected} on:change='{() => window.location.href = "/group/" + selected + "/1"}' class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
        	<option disabled selected>Select a group</option>
				{#each group_data.groups as group}
					<option value={group}>{group}</option>
				{/each}
			</select>
		{:catch error}
			<Error error_text="The API server failed to correctly respond and may be undergoing maintenance. The provided error message is <b>{error.message}</b>" />
		{/await}
	</div>

	<p class="md:text-lg mb-3">
		Alternatively, check out the <a href="/search">Search</a> page if you want to search for
		posts created by a specific user or created during a specific timeframe.
	</p>

	<p>
		If a group that you would like to view is unavailable, please reach out to
		me on <a href="https://twitter.com/lexikiq">Twitter</a> or fire an email to
		<span class="text-pink-500 underline">&lt;my first name&gt; at this domain</span>.
		(Apologies for the obfuscation; I'd like to keep my inbox safe from spam bots 😅)
	</p>

	<p>
		Note: The data on this website comes from Archive Team's periodic archives of Yahoo! Groups between
		October 2015 and October 2018, with most of the Mario Kart groups being archived around late 2017
		and early 2018. This means that a few years of data before the shutdown of Yahoo! Groups are missing.
		Archives of groups after the shutdown of the website exist, however they are in a format which makes
		searching and extracting data from them particularly difficult. This data will nonetheless eventually
		be extracted, however this may take several weeks or months depending on my free time and patience.
	</p>
</main>
