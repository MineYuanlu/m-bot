<script lang="ts">
  import { browser } from '$app/environment';
  const key = 'theme.dark';
  let checked: boolean = browser && localStorage.getItem(key) == 'true';
  $: browser && switchClass(checked);
  if (browser)
    window.addEventListener('storage', () => {
      checked = localStorage.getItem(key) == 'true';
    });
  function switchClass(dark: boolean) {
    const cl = document.getElementsByTagName('body')[0].classList;
    cl.add(dark ? 'dark' : 'light');
    cl.remove(dark ? 'light' : 'dark');
    localStorage.setItem(key, checked.toString());
  }
</script>

<div class="fix">
  <div class="form-check form-switch">
    <input class="form-check-input" type="checkbox" role="switch" id="theme" bind:checked />
    <label class="form-check-label" for="theme">开启暗色背景</label>
  </div>
</div>

<style>
  .fix {
    position: fixed;
    right: 10px;
    top: 10px;
  }
</style>
