<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.favorites.title'))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})
</script>

<template>
  <p>TODO</p>
</template>
