<script lang="ts">
  import type { AddAlert, Flush } from '$def/common';
  import { list } from '$lib/api/manage';
  import AddPlay from './AddPlay.svelte';
  import Play from './Play.svelte';

  export let alert: AddAlert;

  let modCount = 0;
  const flush: Flush = () => {
    modCount++;
  };
</script>

<div class="container">
  <div class="row">
    {#if `${modCount}`}
      {#await list(alert)}
        Loading...
      {:then plays}
        {#if plays}
          {#each plays as play}
            <Play {play} {flush} />
          {:else}
            Nothing...
          {/each}
        {/if}
      {:catch e}
        {(alert('无法列出Play列表: ' + e, 'warning'), '')}
      {/await}
    {/if}
  </div>
</div>
<AddPlay {alert} {flush} />
