<script lang="ts" setup>
import MainToast from '~/components/ui/MainToast.vue'

const sidebar = toReactive(useLocalStorage('sidebar', { open: true }))
</script>

<template>
  <MainToast />

  <div class="flex flex-row overflow-auto" h-screen w-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col content ground" :class="{ open: sidebar.open, closed: !sidebar.open }">
      <Topbar />
      <div class="py-4 px-8 w-full h-full overflow-auto">
        <slot />
      </div>
    </div>
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
