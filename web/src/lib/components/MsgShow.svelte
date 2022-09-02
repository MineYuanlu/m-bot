<script lang="ts">
  import { MsgType, type Msg } from '$def/component';
  import Component from './Component.svelte';
  export let msg: Msg;
  export let setChat: (txt: string, send: boolean) => void;
</script>

[{MsgType[msg.type]}] <Component content={msg.component} {setChat} />
