<script lang="ts" setup>
usePageName('Debug')
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

function closeDebug() {
  navigateTo('/files', { replace: true })
  close()
}

function redirect() {
  if (!isDebugEnabled.value)
    navigateTo('/', { replace: true })

  if (currentRoute.value.path === '/debug')
    navigateTo('/debug/stores', { replace: true })
}

onMounted(redirect)
onUpdated(redirect)
</script>

<template>
  <div class="flex flex-col gap-3">
    <div class="flex justify-between">
      <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />
      <PButton icon="i-tabler-x" label="Close debug" rounded severity="danger" @click="closeDebug" />
    </div>

    <div class="block">
      <NuxtPage />
    </div>
  </div>
</template>
