<script lang="ts">
  import AlertHolder from '$components/AlertHolder.svelte';

  import PlayList from '$components/PlayList.svelte';
  import type { AddAlert } from '$def/common';

  let alert: AddAlert;
</script>

<svelte:head>
  <title>MC BOT</title>
</svelte:head>

<h1>Minecraft Fake Players - Dashboard</h1>
<AlertHolder bind:alert />

<PlayList {alert} />
