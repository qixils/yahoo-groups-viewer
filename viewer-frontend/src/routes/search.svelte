<script context="module" lang="ts">
	export const prerender = true;
</script>

<script lang="ts">
  import type GroupData from '$lib/Constants';
  import Error from "$lib/Error.svelte";
  import LabelFor from "$lib/LabelFor.svelte";
	let groups_promise: Promise<GroupData> = fetch("http://localhost:8080/v1/groups").then(response => response.json())
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
    <form>
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
      <!-- TODO: ensure only integers are entered -->
      <div class="form_block">
        <LabelFor for_id="author_id" text="Author ID"/>
        <input id="author_id" placeholder="581586513" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Author Alias -->
      <div class="form_block">
        <LabelFor for_id="author_alias" text="Author Alias"/>
        <input id="author_alias" placeholder="davidroldans93@..." class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Author Name -->
      <div class="form_block">
        <LabelFor for_id="username" text="Username"/>
        <input id="username" placeholder="davidroldans93" class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500">
      </div>

      <!-- Posted Between -->
      <div class="form_block">
        <LabelFor for_id="posted_between" text="Posted Between"/>
        <div date-rangepicker class="flex items-center" id="posted_between">
          <div class="relative">
            <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
              <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"></path></svg>
            </div>
            <input name="start" type="text" class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5  dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Select date start">
          </div>
          <span class="mx-4 text-gray-500">to</span>
          <div class="relative">
            <div class="flex absolute inset-y-0 left-0 items-center pl-3 pointer-events-none">
              <svg class="w-5 h-5 text-gray-500 dark:text-gray-400" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" clip-rule="evenodd"></path></svg>
            </div>
            <input name="end" type="text" class="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-blue-500 focus:border-blue-500 block w-full pl-10 p-2.5  dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500" placeholder="Select date end">
          </div>
        </div>
      </div>

      <!-- TODO: Submit Button -- use the fancy gradient outline button from https://flowbite.com/docs/components/buttons/-->
      <!-- TODO: Submit Button JS (need input validation and shit) -->
    </form>
  {:catch error}
    <Error error_text="The API server failed to correctly respond and may be undergoing maintenance. The provided error message is <i>{error.message}</i>" />
  {/await}
</main>

<style lang="postcss">
  .form_block {
    @apply mb-3;
  }
</style>
