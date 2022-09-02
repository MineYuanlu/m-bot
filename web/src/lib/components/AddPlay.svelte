<script lang="ts">
  import type { AddAlert, Flush } from '$def/common';

  import type { CreateData } from '$def/Play';
  import { create } from '$lib/api/manage';
  import type { Modal } from 'bootstrap';
  import { onMount } from 'svelte';
  export let alert: AddAlert;
  export let flush: Flush;
  const data: CreateData = {
    player: '',
    host: '',
    port: 25565,
    plan: 'connect\ndelay 1000\nchat /reg uuu888 uuu888\ndelay 1000\nchat /login uuu888\ndelay 1000\nchat awa\ndelay disconnect',
  };
  const createPlay = async () => {
    const id = await create(data, alert);
    if (id) alert('成功创建游戏: ' + id, 'success');
    await flush();
  };

  let element: HTMLElement;
  onMount(() => {
    let modal: Modal;
    import('bootstrap').then((bootstrap) => {
      modal = bootstrap.Modal.getOrCreateInstance(element);
    });
    return () => modal && modal.dispose();
  });
</script>

<div class="container">
  <div class="row">
    <!-- Button trigger modal -->
    <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addPlay">
      添加Play
    </button>

    <!-- Modal -->
    <div
      bind:this={element}
      class="modal fade"
      id="addPlay"
      data-bs-backdrop="static"
      tabindex="-1"
      aria-labelledby="addPlayLabel"
      aria-hidden="true"
    >
      <div class="modal-dialog modal-dialog-centered modal-dialog-scrollable">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title" id="addPlayLabel">添加Play</h5>
            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close" />
          </div>
          <div class="modal-body">
            <form>
              <div class="mb-3">
                <label for="add_player" class="form-label">玩家名称</label>
                <input
                  type="text"
                  class="form-control"
                  id="add_player"
                  aria-describedby="add_player_help"
                  placeholder="username"
                  bind:value={data.player}
                />
                <div id="add_player_help" class="form-text">暂时仅支持盗版玩家</div>
              </div>
              <div class="mb-3">
                <label for="add_host" class="form-label">服务器地址</label>
                <label for="add_port" class="form-label">&nbsp;</label>
                <div class="input-group mb-3">
                  <input
                    id="add_host"
                    type="text"
                    class="form-control"
                    placeholder="host"
                    bind:value={data.host}
                  />
                  <span class="input-group-text">:</span>
                  <input
                    id="add_port"
                    type="number"
                    class="form-control"
                    placeholder="port"
                    bind:value={data.port}
                  />
                </div>
              </div>
              <div class="mb-3">
                <label for="add_plan" class="form-label">计划任务</label>
                <div class="input-group">
                  <textarea class="form-control" id="add_plan" bind:value={data.plan} />
                </div>
              </div>
            </form>
          </div>
          <div class="modal-footer">
            <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
            <button
              type="button"
              class="btn btn-primary"
              data-bs-dismiss="modal"
              on:click={createPlay}>创建</button
            >
          </div>
        </div>
      </div>
    </div>
  </div>
</div>
