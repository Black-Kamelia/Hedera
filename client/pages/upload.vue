<script lang="ts" setup>
const { t } = useI18n()

definePageMeta({
  layout: 'sidebar',
})
</script>

<template>
  <h2 class="text-3xl" style="margin: .375rem 0;">
    {{ t('sidebar.upload') }}
  </h2>
</template>
