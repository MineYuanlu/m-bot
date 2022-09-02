<script lang="ts">
  import { browser } from '$app/environment';
  import type { AddAlert } from '$def/common';
  import { getPlan, setPlan } from '$lib/api/games';
  import type { Modal } from 'bootstrap';
  import { onMount } from 'svelte';

  export let id: number | string;
  export let alert: AddAlert;
  let className = 'btn btn-primary';
  export { className as class };

  /**加载状态 0: ok, 1: loading, 2: error*/
  let loading: 0 | 1 | 2 = 1;

  /**服务器端的计划信息*/
  let plan: string | undefined = undefined;
  $: if (browser && !!alert) flushPlan();
  /**用户编辑的计划信息*/
  let userPlan = '';

  /**UI控制*/
  let element: HTMLElement;
  let modal: Modal;
  onMount(() => {
    import('bootstrap').then((bootstrap) => {
      modal = bootstrap.Modal.getOrCreateInstance(element);
    });
    return () => modal && modal.dispose();
  });

  /**
   * 获取计划
   */
  async function flushPlan() {
    loading = 1;
    plan = await getPlan(id, alert);
    userPlan = plan || '';
    if (plan === undefined) {
      loading = 2;
      modal && modal.hide();
    } else loading = 0;
  }
  /**上传计划*/
  async function uploadPlan() {
    if (loading || plan == userPlan) return;
    loading = 1;
    let err = await setPlan(id, userPlan, alert);
    if (err === true) return flushPlan();
    if (err === false) err = '未知错误';

    alert(err, 'warning');
    loading = 2;
    modal && modal.hide();
  }
</script>

<!-- Button trigger modal -->
<button type="button" class={className} data-bs-toggle="modal" data-bs-target="#editPlans">
  <slot>编辑计划</slot>
</button>

<!-- Modal -->
<div
  bind:this={element}
  class="modal fade"
  id="editPlans"
  tabindex="-1"
  aria-labelledby="editPlansLabel"
  aria-hidden="true"
>
  <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="editPlansLabel">编辑计划</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" />
      </div>
      <div class="modal-body">
        {#if loading === 1}
          加载中...
        {:else if loading === 2}
          加载失败
        {:else}
          <label for="add_plan" class="form-label">
            计划任务
            <textarea class="form-control" id="add_plan" bind:value={plan} />
          </label>
        {/if}
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">返回</button>
        {#if !loading}
          <button
            type="button"
            class="btn btn-secondary"
            data-bs-dismiss="modal"
            disabled={userPlan == plan}
            on:click={uploadPlan}
          >
            完成
          </button>
        {/if}
      </div>
    </div>
  </div>
</div>

<style lang="scss">
  textarea {
    min-height: 50vh;
  }
  .modal-body {
    label,
    textarea {
      width: 100%;
    }
  }
</style>
