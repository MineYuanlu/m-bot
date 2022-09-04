<script lang="ts">
  import { goto } from '$app/navigation';

  import type { Flush } from '$def/common';

  import type { Play } from '$def/Play';
  import { runPlan } from '$lib/api/games';

  export let play: Play;
  export let flush: Flush;
</script>

<div class="play-info col col-md-6 col-xl-4" on:click={() => goto('/console?id=' + play.id)}>
  <table class="table table-borderless">
    <tbody>
      <tr>
        <td>玩家</td>
        <td>{play.player}</td>
      </tr>
      <tr>
        <td>服务器</td>
        <td>{play.host}:{play.port}</td>
      </tr>
      <tr>
        <td>状态</td>
        <td class="online-{play.online ? 't' : 'f'}">{play.online ? '在线' : '离线'}</td>
      </tr>
      <tr>
        <td>计划</td>
        {#if play.plans.running}
          <td>运行中...</td>
        {:else}
          <td>
            <button class="btn btn-outline-primary" on:click={() => runPlan(play.id, alert, flush)}
              >运行计划</button
            >
          </td>
        {/if}
      </tr>
    </tbody>
  </table>
  <div class="id">{play.id}</div>
</div>

<style lang="scss">
  .play-info {
    margin: 10px;
    box-shadow: rgba(0, 0, 0, 0.3) 1px 1px 3px;
    position: relative;
    cursor: pointer;
    &:hover {
      transition: box-shadow 0.3s;
      box-shadow: rgba(0, 0, 0, 0.5) 2px 2px 4px;
    }
    table {
      border: none;
      td {
        line-height: 24px;
        height: 24px;
        vertical-align: middle;
        // padding: 10px;
        color: var(--color);
        &:first-child {
          text-align: right;
          border-right: rgba(0, 0, 0, 0.3) 1px solid;
        }
      }
    }
    .online-t {
      color: green;
    }
    .online-f {
      color: red;
    }
    .id {
      position: absolute;
      right: 3px;
      bottom: 3px;
      color: rgba(0, 0, 0, 0.5);
    }
  }
</style>
