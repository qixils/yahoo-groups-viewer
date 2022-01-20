<script lang="ts">
    import "../app.postcss";
</script>

<div id="meta-container">
  <div id="main-container"><slot></slot></div>

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
</div>

<style>
  #meta-container {
    padding: 1rem;
    margin: 0;
    display: flex;
    flex-direction: column;
    min-height: 100vh;
  }

  footer {
    margin-top: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
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
