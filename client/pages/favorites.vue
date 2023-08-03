<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.favorites.title'))
definePageMeta({
  layout: 'bottombar',
  middleware: ['auth'],
})
</script>

<template>
  <p>TODO</p>
</template>
