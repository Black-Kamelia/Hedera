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
onMounted(() => {
  if (currentRoute.value.path === '/debug')
    navigateTo('/debug/stores', { replace: true })
})
</script>

<template>
  <div class="flex flex-col gap-3">
    <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />

    <div class="block">
      <NuxtPage />
    </div>
  </div>
</template>
