<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.configuration.title'))
definePageMeta({
  layout: 'main',
  middleware: ['auth', 'restricted'],
})

const menus = computed(() => [
  {
    label: t('pages.configuration.menu.general'),
    icon: 'i-tabler-adjustments-filled',
    path: '/configuration/general',
  },
  {
    label: t('pages.configuration.menu.users'),
    icon: 'i-tabler-users',
    path: '/configuration/users',
  },
  {
    label: t('pages.configuration.menu.maintenance'),
    icon: 'i-tabler-tool',
    path: '/configuration/maintenance',
  },
])

const { currentRoute } = useRouter()

function redirect(currentRoute: { path: string }) {
  if (currentRoute.path === '/configuration') {
    navigateTo('/configuration/general', { replace: true })
  }
}

watch(currentRoute, redirect, { immediate: true })
</script>

<template>
  <div class="flex flex-col">
    <div class="flex flex-col sticky top-0 bg-[var(--ground)] z-100">
      <div class="adaptive-width flex overflow-y-auto self-center py-4 px-8 w-full xl:w-70%">
        <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />
      </div>
      <hr>
    </div>
    <div class="adaptive-width self-center px-8 my-5 w-full xl:w-70%">
      <NuxtPage />
    </div>
  </div>
</template>

<style scoped>
.adaptive-width {
  transition: width 0.3s ease, padding 0.3s ease, align-self 0.3s ease;
}
</style>
