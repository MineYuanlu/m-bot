<script lang="ts">
  import DarkBg from '$components/DarkBg.svelte';

  import 'bootstrap/dist/css/bootstrap.min.css';
  import '../app.css';
  export const prerender = true;
</script>

<DarkBg />
<slot />
