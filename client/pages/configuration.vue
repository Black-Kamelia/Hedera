<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.configuration.title'))
definePageMeta({
  layout: 'main',
  middleware: ['auth'],
})
</script>

<template>
  <p>TODO</p>
</template>
