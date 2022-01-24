<script lang="ts">
  import sanitizeHtml from 'sanitize-html';
  import type Message from '$lib/Constants';
  export let messages: Message[];
  const dt_options: Intl.DateTimeFormatOptions = {
    dateStyle: 'medium',
    timeStyle: 'short'
  };

  function split(body: string): string[] {
    const lower_body: string = body.toLowerCase();
    let lines: string[] = body.split("\n\n").filter(value => value.length > 0);
    if (!lower_body.includes("<br>") && !lower_body.includes("</p>") && !lower_body.includes("</div>")) {
      lines = lines.map(value => value.replaceAll("\n", "<br>"));
    }
    return lines;
  }

  function filterHTML(body: string): string {
    return sanitizeHtml(body, {
      allowedTags: [
        "table", "tbody", "td", "tfoot", "th", "thead", "tr", "col", "colgroup",
        "h1", "h2", "h3", "h4", "h5", "h6",
        "pre", "ul", "ol", "li", "img", "a", "strong", "b", "i", "p", "div"
      ],
      allowedSchemes: ['http', 'https', 'ftp']
    });
  }

  function localize(unix: number): string {
    const date: Date = new Date(unix*1000);
    return date.toLocaleString(undefined, dt_options);
  }
</script>

<div id="messages">
  {#each messages as message (message.id)}
    <div class="py-2 border-zinc-200 dark:border-zinc-800 border-b-2 last:border-b-0">
      <p class="my-1">
        <a href="/user/{message.authorId}" class="no-url font-extrabold">{message.alias}</a>
        <span class="text-zinc-500 ml-1.5">{localize(message.postDate)}</span>
      </p>
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