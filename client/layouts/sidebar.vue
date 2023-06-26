<script lang="ts" setup>
import { useStorage } from '@vueuse/core'

const sidebar = toReactive(useStorage('sidebar', { open: true }))
const sidebarWidth = computed(() => sidebar.open ? '19em' : '5em')
</script>

<template>
  <div class="flex flex-row overflow-auto" h-screen w-screen>
    <Sidebar />
    <div class="flex-grow flex flex-col content ground">
      <Topbar />
      <div class="py-4 px-8 w-full h-full overflow-auto">
        <slot />
      </div>
    </div>
  </div>
</template>

<style>
.content {
  transition: max-width 0.3s ease, width 0.3s ease;
  max-width: calc(100% - v-bind(sidebarWidth));
}
</style>
