<script lang="ts">
  import { browser } from '$app/environment';

  import {
    ClickAction,
    HoverAction,
    MsgType,
    type ClickEvent,
    type HoverEvent,
    type Msg,
    type TextContent,
  } from '$def/component';
  import { copy } from '$helpers/copy';
  import { random } from '$helpers/random';
  import { onMount } from 'svelte';
  export let content: TextContent[];
  export let setChat: (txt: string, send: boolean) => void;

  const click = (c?: ClickEvent) => {
    if (!c) return;
    switch (c.action) {
      case 'OPEN_FILE':
      case 'CHANGE_PAGE':
        return;
      case 'COPY_TO_CLIPBOARD':
        return copy(c.value);
      case 'OPEN_URL':
        return window.open(c.value);
      case 'RUN_COMMAND':
        return setChat(c.value, true);
      case 'SUGGEST_COMMAND':
        return setChat(c.value, false);
    }
  };
  let hoverShow = true;
  let left = 0,
    top = 0;
  let hoverEvent: HoverEvent | undefined;
  let clickEvent: ClickEvent | undefined;

  let obfuscatedString = '';

  if (browser)
    ((tcs: TextContent[]) => {
      if (!tcs || !tcs.length) return;
      const len = Math.max(...tcs.map((x) => x.t.length));
      if (!(len > 0)) return;
      onMount(() => {
        const timer = setInterval(
          () => (obfuscatedString = random(len)),
          (60 + Math.random() * 5) | 0,
        );
        return () => clearInterval(timer);
      });
    })(content.filter((x) => x.s && x.s.split(' ').includes('k')));
</script>

{#each content as { s, t, h, c }}
  {@const ss = s ? s.split(' ') : []}
  {@const color = ss.length && ss[0].startsWith('#') ? ss[0].split('|') : []}
  {@const obfuscated = ss.includes('k')}
  <span
    style:color={color[0]}
    style:background-color={color[1]}
    class={ss
      .filter((x) => !x.startsWith('#') && x !== 'k')
      .map((x) => `style-${x}`)
      .join(' ')}
    on:click={() => click(c)}
    on:mousemove={(e) => {
      left = e.clientX;
      top = e.clientY;
      hoverEvent = h;
      clickEvent = c;
    }}
    on:mouseover={() => (hoverShow = true)}
    on:focus={() => void 0}
    on:blur={() => void 0}
    on:mouseout={() => (hoverShow = false)}
  >
    {#each t.split('\n') as line}
      {obfuscated ? obfuscatedString.substring(0, line.length) : line}
    {/each}
  </span>
{/each}
{#if hoverShow && (hoverEvent || clickEvent)}
  <div class="hover" style:left="{left + 5}px" style:top="{top + 5}px">
    {#if hoverEvent}
      悬浮{HoverAction[hoverEvent.action]}: <br />
      <svelte:self content={hoverEvent.value} />
    {/if}
    {#if hoverEvent && clickEvent}
      <hr />
    {/if}
    {#if clickEvent}
      点击将会[{ClickAction[clickEvent.action]}]: <br />
      {clickEvent.value}
    {/if}
  </div>
{/if}

<style>
  .style-l {
    font-weight: bold;
  }
  .style-m {
    text-decoration: line-through;
  }
  .style-n {
    text-decoration: underline;
  }
  .style-m,
  .style-n {
    text-decoration: underline line-through;
  }
  .style-o {
    font-style: oblique;
  }
  .hover {
    position: fixed;
    background-color: #2980b9;
    color: #fff;
    border: var(--color) 2px solid;
    padding: 5px;
    border-radius: 3px;
  }
</style>
