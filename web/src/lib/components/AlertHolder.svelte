<script lang="ts">
  import type { AlertType } from '$def/bootstrap';
  import type { AddAlert } from '$def/common';
  import Alert from './Alert.svelte';

  export let alerts: { type: AlertType; message: any }[] = [];
  export const alert: AddAlert = (message, type) => {
    alerts.push({ message, type });
    alerts = alerts;
  };
</script>

<div class="alerts">
  {#each alerts as alert}
    <Alert {...alert} />
  {/each}
</div>
