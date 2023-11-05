<script setup lang="ts">
const { t } = useI18n()

usePageName(() => t('pages.register.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
</script>

<template>
  <AuthContainer :initial-state="RegisterState" />
</template>
