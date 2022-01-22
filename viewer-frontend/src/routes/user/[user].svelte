<script context="module" lang="ts">
  /** @type {import('@sveltejs/kit').Load} */
  export async function load({ params, fetch, session, stuff }) {
    const user: any = params.user
    const user_res: Response = await fetch(`http://localhost:8080/v1/user/${user}`);
    const user_res_json: any = await user_res.json();

    // TODO: handle timeouts & rejects

    if (user_res_json.id === undefined && user_res_json.error === undefined) {
      return {
        props: {
          "user_id": user,
          "user": {
            error: 'The API server threw an unexpected exception (' + user_res.status + ')',
            getError() { return this.error; }
          }
        }
      }
    }

    return {
      props: {
        "user_id": user,
        "user": user_res_json
      }
    }
  }
</script>

<script lang="ts">
  import Error from "$lib/Error.svelte";
  import Info from "$lib/Info.svelte";
import Warning from "$lib/Warning.svelte";

  interface User {
    id: number,
    userName: string | null,
    knownAliases: string[],
    knownGroups: string[],
    fakeAccount: boolean,
    error?: string
  }
  export let user_id: any;
  export let user: User;

  let displayName: string;
  if (user.error !== undefined) {
    displayName = user_id.toString();
  } else if (user.knownAliases.length === 1) {
    displayName = user.knownAliases[0];
  } else if (user.userName !== null) {
    displayName = user.userName;
  } else {
    displayName = user.id.toString();
  }
</script>

<svelte:head>
  <title>Yahoo! Groups Viewer ∙ {displayName}'s Profile</title>
  <meta name="description" content="{displayName}'s profile from the defunct website Yahoo! Groups.">
  <meta property="og:description" content="{displayName}'s profile from the defunct website Yahoo! Groups.">
  <meta property="og:url" content="https://yahoo.qixils.dev/user/{user}">
  <link rel="canonical" href="https://yahoo.qixils.dev/user/{user}">
</svelte:head>

<header class="text-center">
  <h1 class="yahoo-header"><a href="/" class="no-url">Yahoo! Groups Viewer</a></h1>
  <h2 class="text-lg">{displayName}'s archived account information</h2>
  <hr class="mt-4 mb-5 border-y-zinc-400 dark:border-y-zinc-700">
</header>

<main>
  {#if user.error !== undefined}
    <Error error_text="An error occurred while trying to load this page: {user.error}" />
  {:else}
    <Warning warning_text="The following information has been obtained from this website's small collection of indexed Yahoo! Groups
      archives. This is but a tiny fraction of all the data recovered from the website." />

    {#if user.fakeAccount}
      <Info info_text="<b>Note:</b> The following profile has been auto-generated for a user who participated in Yahoo! Groups
        without creating an account. Guest users with the same display name have been lumped together into one user." />
      <p class="text-2xl p-1 pl-3 border-l-4 border-zinc-900 dark:border-zinc-50">{displayName}</p>
    {:else if displayName !== user.userName && user.userName !== null}
      <p class="text-2xl p-1 pl-3 border-l-4 border-zinc-900 dark:border-zinc-50">
        {displayName}
        <span class="text-zinc-500">({user.userName})</span>
      </p>
    {:else}
     <p class="text-2xl p-1 pl-3 border-l-4 border-zinc-900 dark:border-zinc-50">{displayName}</p>
    {/if}

    <table class="table-auto border-collapse mt-3">
      <tr>
        <th>Account ID</th>
        <td>#{user.id}</td>
      </tr>
      <tr>
        <th>Username</th>
        <td>
          {#if user.userName !== null}
            {user.userName}
          {:else}
            &lt;not applicable&gt;
          {/if}
        </td>
      </tr>
      <tr>
        <th>Known Aliases</th>
        <td>
          <ul>
            {#each user.knownAliases as alias}
              <li>{alias}</li>
            {/each}
          </ul>
        </td>
      </tr>
      <tr>
        <th>Known Groups</th>
        <td>
          <ul>
            {#each user.knownGroups as group}
              <li><a href="/search/{group}/?authorId={user.id}">{group}</a></li>
            {/each}
          </ul>
        </td>
      </tr>
    </table>
  {/if}
</main>

<style lang="postcss">
  th, td {
    @apply p-2 border-2 border-zinc-500;
  }
</style>