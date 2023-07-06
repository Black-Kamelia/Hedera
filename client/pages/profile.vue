<script lang="ts" setup>
const { t } = useI18n()
const { user } = useAuth()

usePageName(() => t('pages.profile.title', { username: user?.value?.username }))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const menus = [
  {
    label: 'Informations',
    icon: 'i-tabler-user-circle',
    path: '/profile/details',
  },
  {
    label: 'Sécurité',
    icon: 'i-tabler-shield',
    path: '/profile/security',
  },
  {
    label: 'Jetons personnels',
    icon: 'i-tabler-key',
    path: '/profile/tokens',
  },
  {
    label: 'Paramètres',
    icon: 'i-tabler-settings',
    path: '/profile/settings',
  },
]
</script>

<template>
  <div class="flex flex-col">
    <div class="flex flex-col sticky top-0 bg-[var(--ground)] z-100">
      <div class="t flex overflow-y-auto self-center py-4 px-8 w-full xl:w-70%">
        <TabNavigation :items="menus" :route-match="(path, item) => path.endsWith(item.path)" />
      </div>
      <hr>
    </div>
    <div class="t self-center px-8 mt-5 w-full xl:w-70%">
      <NuxtPage />
    </div>
  </div>
</template>

<style scoped>
.t {
  transition: width 0.3s ease, padding 0.3s ease, align-self 0.3s ease;
}
</style>
