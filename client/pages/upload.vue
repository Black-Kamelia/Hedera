<script lang="ts" setup>
const { t } = useI18n()

usePageName(t('pages.upload.page_name'))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})
</script>

<template>
  <p>TODO</p>
</template>
