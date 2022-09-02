<script lang="ts">
  import type { AlertType } from '$def/bootstrap';
  import type { Alert } from 'bootstrap';
  import { onMount } from 'svelte';

  export let message: any;
  export let type: AlertType;
  let element: HTMLElement;
  onMount(() => {
    let modal: Alert;
    import('bootstrap').then((bootstrap) => {
      modal = bootstrap.Alert.getOrCreateInstance(element);
    });
    return () => modal && modal.dispose();
  });
  export let close = false;
</script>

{#if !close}
  <div>
    <div class="alert alert-{type} alert-dismissible" role="alert" bind:this={element}>
      {message}
      <button
        type="button"
        class="btn-close"
        data-bs-dismiss="alert"
        aria-label="Close"
        on:click={() => (close = true)}
      />
    </div>
  </div>
{/if}
