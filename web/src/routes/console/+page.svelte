<script lang="ts">
  import { page } from '$app/stores';
  import AlertHolder from '$components/AlertHolder.svelte';
  import MsgShow from '$components/MsgShow.svelte';
  import type { AddAlert } from '$def/common';
  import type { Msg } from '$def/component';
  import { chat, connect, disconnect, getInfoWs, runPlan, stopPlan } from '$lib/api/games';
  import { goto } from '$app/navigation';
  import Plans from '$components/Plans.svelte';
  const id = $page.url.searchParams.get('id') || 'undefined';
  if (!/^[0-9]+$/.test(id)) goto('/');
  let alert: AddAlert;
  let msgs: Msg[] = [];
  msgs.push(
    JSON.parse(
      `{"type":"DISCONNECT","component":[{"t":"123","c":{"action":"RUN_COMMAND","value":"123"}}]}`,
    ),
  );
  //   msgs.push(
  //     JSON.parse(
  //       `{"type":"DISCONNECT","component":[{"t":"[yuanlu] "},{"s":"#555555","t":"["},{"s":"#ffaa00 l m n o k","t":"喵奈登录系统"},{"s":"#555555","t":"] "},{"s":"#ffffff","t":"同样的用户名现在在线且已经登录了！","h":{"action":{"name":"show_text"}}}]}`,
  //     ),
  //   );
  //   msgs = Array(50).fill(msgs[0]);
  const ws = getInfoWs($page.url);
  ws.onmessage = ({ data }) => {
    if (typeof data !== 'string' || !data || data == '-') return;
    msgs.push(JSON.parse(data));
    msgs = msgs;
  };
  ws.onerror = () => alert('发生错误', 'warning');
  ws.onclose = () => alert('链接中断', 'danger');

  let chatString = '';

  /**发送聊天*/
  const send = async () => {
    await chat(id, chatString, alert);
    chatString = '';
  };
  /**设置聊天*/
  const setChat = async (txt: string, needSend: boolean) => {
    chatString = txt;
    if (needSend) await send();
  };
</script>

<h1>Minecraft Fake Players - Console</h1>
<AlertHolder bind:alert />

<div class="container msgs">
  {#each msgs as msg}
    <div class="msg"><MsgShow {msg} {setChat} /></div>
  {/each}
</div>
<br />
<div class="container">
  <div class="input-group mb-3">
    <input
      type="text"
      class="form-control"
      placeholder="发送聊天 / 命令..."
      bind:value={chatString}
      on:keydown={(e) => e.key === 'Enter' && send()}
    />
    <button class="btn btn-primary" type="button" on:click={() => send()}>发送</button>
  </div>
  <div class="btn-toolbar mb-3 " role="toolbar">
    <div class="input-group me-3">
      <button class="btn btn-primary" type="button" on:click={() => goto('/')}>返回</button>
    </div>
    <div class="input-group me-3">
      <button class="btn btn-primary" type="button" on:click={() => connect(id, alert)}>
        进入游戏
      </button>
      <button class="btn btn-primary" type="button" on:click={() => disconnect(id, alert)}>
        退出游戏
      </button>
    </div>

    <div class="input-group me-3">
      <button class="btn btn-primary" type="button" on:click={() => runPlan(id, alert)}>
        执行计划
      </button>
      <button class="btn btn-primary" type="button" on:click={() => stopPlan(id, alert)}>
        终止计划
      </button>
      <Plans {id} {alert} />
    </div>
  </div>
</div>

<style lang="scss">
  .msgs {
    border: 1px solid rgba($color: var(--color), $alpha: 0.3);
    height: 70vh;
    overflow-y: scroll;
  }
  .msg {
    font-size: 1.1rem;
    line-height: 1.2;
    &:hover {
      box-shadow: #ccc 1px 1px 1px;
      transition: box-shadow 0.1s;
    }
  }
</style>
