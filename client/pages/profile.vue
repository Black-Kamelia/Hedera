<script lang="ts" setup>
const { t } = useI18n()
const { user } = storeToRefs(useAuth())

usePageName(() => t('pages.profile.title', { username: user?.value?.username }))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const menus = computed(() => [
  {
    label: t('pages.profile.menu.details'),
    icon: 'i-tabler-user-circle',
    path: '/profile/details',
  },
  {
    label: t('pages.profile.menu.security'),
    icon: 'i-tabler-shield',
    path: '/profile/security',
  },
  // {
  //   label: t('pages.profile.menu.sessions'),
  //   icon: 'i-tabler-devices',
  //   path: '/profile/sessions',
  // },
  {
    label: t('pages.profile.menu.tokens'),
    icon: 'i-tabler-key',
    path: '/profile/tokens',
  },
  {
    label: t('pages.profile.menu.settings'),
    icon: 'i-tabler-settings',
    path: '/profile/settings',
  },
])

const { currentRoute } = useRouter()

function redirect(currentRoute: { path: string }) {
  if (currentRoute.path === '/profile') {
    navigateTo('/profile/details', { replace: true })
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
