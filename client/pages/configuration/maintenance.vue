<script lang="ts" setup>
const { t } = useI18n()
</script>

<template>
  <div class="flex flex-col gap-3 relative">
    <h1 class="text-2xl">
      {{ t('pages.configuration.maintenance.title') }}
    </h1>

    <ClearThumbnailCache />
  </div>
</template>
