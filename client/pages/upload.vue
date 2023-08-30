<script lang="ts" setup>
const { t } = useI18n()

usePageName(() => t('pages.upload.title'))
definePageMeta({ layout: 'sidebar', middleware: ['auth'] })
</script>

<template>
  <div class="py-4 px-8 h-full flex flex-col gap-4">
    <div class="p-card p-0 overflow-hidden flex-grow">
      <FileDrop />
    </div>
  </div>
</template>
