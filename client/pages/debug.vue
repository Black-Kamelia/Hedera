<script lang="ts" setup>
usePageName(() => 'Debug')
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const menus = [
  {
    label: 'Stores',
    icon: 'i-tabler-database-dollar',
    path: '/debug/stores',
  },
  {
    label: 'Layout',
    icon: 'i-tabler-dimensions',
    path: '/debug/layout',
  },
  {
    label: 'Colors',
    icon: 'i-tabler-palette',
    path: '/debug/colors',
  },
  {
    label: 'Components',
    icon: 'i-tabler-components',
    path: '/debug/components',
  },
]

const { currentRoute } = useRouter()
const { isDebugEnabled, close } = useDebug()

function redirect([isDebugEnabled, currentRoute]: [boolean | undefined, { path: string }]) {
  if (!isDebugEnabled)
    navigateTo('/files', { replace: true })

  if (currentRoute.path === '/debug')
    navigateTo('/debug/stores', { replace: true })
}

watch([isDebugEnabled, currentRoute], redirect, { immediate: true })
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex justify-between">
      <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />
      <PButton icon="i-tabler-x" label="Close debug" rounded severity="danger" @click="close" />
    </div>

    <div class="block">
      <NuxtPage />
    </div>
  </div>
</template>
