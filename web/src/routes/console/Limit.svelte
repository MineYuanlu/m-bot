<script lang="ts">
  import { browser } from '$app/environment';
  const key = 'msg.limit';
  export let limit = Number((browser && localStorage.getItem(key)) || 128);
  $: browser && setLimit(limit);
  if (browser)
    window.addEventListener('storage', () => {
      limit = Number(localStorage.getItem(key) || 128);
    });
  function setLimit(limit: number) {
    localStorage.setItem(key, limit.toString());
  }
</script>
