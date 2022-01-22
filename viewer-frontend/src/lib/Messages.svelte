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
  export let messages: Message[];

  function split(body: string): string[] {
    return body.split("\n\n");
  }

  function filterHTML(body: string): string {
    return body; // TODO basic HTML filtering (remove styles and tags besides <a> and <table>-related stuff)
  }
</script>

<div id="messages">
  {#each messages as message (message.id)}
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