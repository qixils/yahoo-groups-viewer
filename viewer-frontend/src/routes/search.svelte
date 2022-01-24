<script context="module" lang="ts">
	export const prerender = true;
</script>

<script lang="ts">
  import type GroupData from '$lib/Constants';
  import Error from "$lib/Error.svelte";
  import LabelFor from "$lib/LabelFor.svelte";
	const groups_promise: Promise<GroupData> = fetch("https://yahoo.qixils.dev/v1/groups").then(response => response.json());
  const pattern = "[A-Za-z0-9.@_]{4,40}"; // pattern for usernames; here because the {} causes issues when inline

  function unixSecondsOf(date: string): number {
    return Math.round(new Date(date).valueOf() / 1000);
  }

  function onSubmit(event: SubmitEvent) {
    const button: HTMLButtonElement = event.submitter;
    const form = button.form;

    const group_elem: HTMLSelectElement = form.elements['group'];
    const id_elem: HTMLInputElement = form.elements['author_id'];
    const alias_elem: HTMLInputElement = form.elements['author_alias'];
    const name_elem: HTMLInputElement = form.elements['username'];
    const after_elem: HTMLInputElement = form.elements['posted_after'];
    const before_elem: HTMLInputElement = form.elements['posted_before'];

    const queries: string[] = new Array();
    if (id_elem.value !== "") {
      queries.push("authorId=" + id_elem.value);
    }
    if (alias_elem.value !== "") {
      queries.push("displayName=" + alias_elem.value);
    }
    if (name_elem.value !== "") {
      queries.push("userName=" + name_elem.value);
    }
    if (after_elem.value !== "") {
      queries.push("after=" + unixSecondsOf(after_elem.value));
    }
    if (before_elem.value !== "") {
      queries.push("before=" + unixSecondsOf(before_elem.value));
    }
    window.location.href = `/search/${group_elem.value}?${queries.join('&')}`;
  }
</script>

<svelte:head>
	<title>Yahoo! Groups Viewer</title>
	<meta name="description" content="Advanced search tool for finding specific emails sent to groups on the defunct website Yahoo! Groups.">
	<meta property="og:description" content="Advanced search tool for finding specific emails sent to groups on the defunct website Yahoo! Groups.">
</svelte:head>

<header class="text-center">
  <h1 class="yahoo-header"><a href="/" class="no-url">Yahoo! Groups Viewer</a></h1>
  <p class="md:text-lg">Advanced Search Tool</p>
</header>

<main class="max-w-2xl mx-auto">
  {#await groups_promise}
    <p><i>Loading group data...</i></p>
  {:then group_data}
    <!-- Form theming adopted from https://flowbite.com/docs/components/forms/ -->
    <form id="search_form" method="get" on:submit={onSubmit} onsubmit="return false;">
      <!-- Group Selector -->
      <div class="form_block">
        <LabelFor for_id="group" required={true} text="Group"/>
        <select required id="group" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
          {#each group_data.groups as group}
            <option>{group}</option>
          {/each}
        </select>
      </div>

      <!-- Author ID -->
      <div class="form_block">
        <LabelFor for_id="author_id" text="Author ID"/>
        <!-- TODO: ensure the max is accurate for other groups -->
        <input id="author_id" placeholder="581586513" type="number" step="any" min=1 max=999999999 class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Author Alias -->
      <div class="form_block">
        <LabelFor for_id="author_alias" text="Author Alias"/>
        <input id="author_alias" placeholder="davidroldans93@..." type="text" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Author Name -->
      <div class="form_block">
        <LabelFor for_id="username" text="Username"/>
        <!-- ignore the error on the following line (it's wrong) -->
        <input id="username" placeholder="davidroldans93" type="text" pattern={pattern} class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Posted After -->
      <div class="form_block">
        <LabelFor for_id="posted_after" text="Posted After"/>
        <div class="relative">
          <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
            <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"></path></svg>
          </div>
          <input datepicker type="date" id="posted_after" class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Select date">
        </div>
      </div>

      <!-- Posted Before -->
      <div class="form_block">
        <LabelFor for_id="posted_before" text="Posted Before"/>
        <div class="relative">
          <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
            <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"></path></svg>
          </div>
          <input datepicker type="date" id="posted_before" class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Select date">
        </div>
      </div>

      <!-- TODO (post-release): disable button if no input has been entered -->
      <button class="mt-1.5 relative inline-flex items-center justify-center p-0.5 mb-2 mr-2 overflow-hidden text-sm font-medium text-gray-900 rounded-lg group bg-gradient-to-br from-purple-500 to-pink-500 group-hover:from-purple-500 group-hover:to-pink-500 hover:text-white dark:text-white focus:ring-4 focus:ring-purple-200 dark:focus:ring-purple-800">
        <span class="relative px-5 py-2.5 transition-all ease-in duration-75 bg-white dark:bg-gray-900 rounded-md group-hover:bg-opacity-0 text-base">
            Search
        </span>
      </button>
    </form>
  {:catch error}
    <Error error_text="The API server failed to correctly respond and may be undergoing maintenance. The provided error message is <i>{error.message}</i>" />
  {/await}
</main>

<style lang="postcss">
  .form_block {
    @apply mb-3;
  }

  /*-- Disable up/down arrows for number input: --*/

  /* Chrome, Safari, Edge, Opera */
  input::-webkit-outer-spin-button,
  input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
  }

  /* Firefox */
  input[type=number] {
    -moz-appearance: textfield;
  }
</style>
