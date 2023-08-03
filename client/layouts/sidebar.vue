<script lang="ts" setup>
const sidebar = useLocalStorage('sidebar', { open: true })
</script>

<template>
  <div class="flex flex-row overflow-auto" h-screen w-screen>
    <Sidebar />
    <div class="flex-grow content ground" :class="{ open: sidebar.open, closed: !sidebar.open }">
      <Topbar />
      <div class="w-full h-full overflow-auto">
        <slot />
      </div>
    </div>

    <MainToast />
  </div>
</template>

<style lang="scss">
.content {
  transition: max-width 0.3s cubic-bezier(0.25, 1, 0.5, 1), width 0.3s cubic-bezier(0.25, 1, 0.5, 1);
  display: grid;
  grid-template-rows: auto 1fr;

  &.open {
    max-width: calc(100% - var(--sidebar-width-open));
  }

  &.closed {
    max-width: calc(100% - var(--sidebar-width-collapsed));
  }
}
</style>
