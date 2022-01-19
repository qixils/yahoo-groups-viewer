<script lang="ts">
  export let current: number;
  export let end: number;

  let has_next_page: boolean = current !== end;
  let has_prev_page: boolean = current !== 1;
  let pages: number[] = [];
  for (let i = current-2; i < (current+5) && pages.length < 5; i++) {
    if (i > 0) {
      pages.push(i);
    }
  }
</script>

<!-- TODO: horizontal center -->
<nav aria-label="Page Navigation">
  <ul class="inline-flex items-center -space-x-px">

    {#if has_prev_page}
      <li>
        <a href="{current-1}" class="block py-2 px-3 ml-0 leading-tight text-gray-500 bg-white rounded-l-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
          <span class="sr-only">Previous</span>
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M12.707 5.293a1 1 0 010 1.414L9.414 10l3.293 3.293a1 1 0 01-1.414 1.414l-4-4a1 1 0 010-1.414l4-4a1 1 0 011.414 0z" clip-rule="evenodd"></path></svg>
        </a>
      </li>
    {:else}
      <!-- TODO -->
    {/if}

    {#each pages as page}
      {#if page === current}
        <li>
          <a href="{page}" aria-current="page" class="z-10 py-2 px-3 leading-tight text-blue-600 bg-blue-50 border border-blue-300 hover:bg-blue-100 hover:text-blue-700 dark:border-gray-700 dark:bg-gray-700 dark:text-white">{page}</a>
        </li>
      {:else}
        <li>
          <a href="{page}" class="py-2 px-3 leading-tight text-gray-500 bg-white border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">{page}</a>
        </li>
      {/if}
    {/each}

    {#if has_next_page}
      <li>
        <a href="{current+1}" class="block py-2 px-3 leading-tight text-gray-500 bg-white rounded-r-lg border border-gray-300 hover:bg-gray-100 hover:text-gray-700 dark:bg-gray-800 dark:border-gray-700 dark:text-gray-400 dark:hover:bg-gray-700 dark:hover:text-white">
          <span class="sr-only">Next</span>
          <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20" xmlns="http://www.w3.org/2000/svg"><path fill-rule="evenodd" d="M7.293 14.707a1 1 0 010-1.414L10.586 10 7.293 6.707a1 1 0 011.414-1.414l4 4a1 1 0 010 1.414l-4 4a1 1 0 01-1.414 0z" clip-rule="evenodd"></path></svg>
        </a>
      </li>
    {:else}
        <!-- TODO -->
    {/if}

  </ul>
</nav>
    