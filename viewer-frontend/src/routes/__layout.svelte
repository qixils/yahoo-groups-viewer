<script lang="ts">
    import "../app.postcss";
</script>

<slot></slot>

<!-- TODO: snap footer to bottom -->
<footer class="text-zinc-800 dark:text-zinc-300">
  <p>
    Created with ❤️ by
    <a class="text-fuchsia-700 underline" href="https://qixils.dev/">qixils.dev</a>
  </p>
  <p>
    This website's
    <a class="text-fuchsia-700 underline" href="https://github.com/qixils/yahoo-groups-viewer">source code</a>
    has been made publicly available under the
    <a class="text-fuchsia-700 underline" href="https://github.com/qixils/yahoo-groups-viewer/blob/master/LICENSE">MIT license</a>.
  </p>
</footer>

<style>
  footer {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    padding: 40px 40px 0 40px;
  }

  footer a {
    font-weight: bold;
  }

  @media (min-width: 480px) {
    footer {
      padding: 40px 0 0 0;
    }
  }
</style>
