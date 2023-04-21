<script lang="ts" setup>
import usePageName from '~/composables/usePageName'

const { logout } = useAuth()

const pageName = usePageName()
</script>

<template>
  <div class="top-bar flex items-center gap-4 px-8 py-5 h-20 w-full bg-[var(--surface-overlay)]">
    <h2 class="text-3xl w-full">
      {{ pageName }}
    </h2>
    <QuotaPreviewer :quota="536870912" :max="1073741824" />
    <div class="flex flex-row gap-2">
      <PButton icon="i-tabler-language" text rounded />
      <PButton icon="i-tabler-bell" text rounded />
      <PButton icon="i-tabler-logout" text rounded @click="logout()" />
    </div>
  </div>
</template>

<style scoped>
.top-bar {
  border-bottom: 1px solid var(--surface-border);
}
</style>
