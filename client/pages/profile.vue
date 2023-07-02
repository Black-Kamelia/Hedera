<script lang="ts" setup>
const { t } = useI18n()
const { user } = useAuth()

usePageName(t('pages.profile.title', { username: user?.value?.username }))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const menus = [
  {
    label: 'Details',
    icon: 'i-tabler-user-circle',
    path: '/profile/details',
  },
  {
    label: 'Security',
    icon: 'i-tabler-shield',
    path: '/profile/security',
  },
  {
    label: 'Tokens',
    icon: 'i-tabler-key',
    path: '/profile/tokens',
  },
  {
    label: 'Settings',
    icon: 'i-tabler-settings',
    path: '/profile/settings',
  },
]
</script>

<template>
  <div class="flex justify-center">
    <div class="t lg:w-full xl:w-70%">
      <div class="sticky top-0 bg-[var(--ground)] z-100">
        <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />
        <hr class="my-5">
      </div>
      <NuxtPage />
    </div>
  </div>
</template>

<style scoped>
.t {
  transition: width 0.3s ease;
}
</style>
