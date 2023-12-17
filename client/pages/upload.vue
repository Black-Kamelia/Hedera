<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.upload.title'))
definePageMeta({
  layout: 'main',
  middleware: ['auth'],
})
</script>

<template>
  <div class="py-4 px-4 sm:px-8 h-full max-h-full">
    <FileUpload />
  </div>
</template>
