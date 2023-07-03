<script lang="ts" setup>
const sidebar = useLocalStorage('sidebar', { open: true })
</script>

<template>
  <div class="flex flex-row overflow-auto" h-screen w-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col content ground" :class="{ open: sidebar.open, closed: !sidebar.open }">
      <Topbar />
      <div class="py-4 px-8 w-full h-full overflow-auto">
        <slot />
      </div>
    </div>

    <MainToast />
  </div>
</template>

<style lang="scss">
.content {
  transition: max-width 0.3s ease, width 0.3s ease;

  &.open {
    max-width: calc(100% - var(--sidebar-width-open));
  }

  &.closed {
    max-width: calc(100% - var(--sidebar-width-collapsed));
  }
}
</style>
