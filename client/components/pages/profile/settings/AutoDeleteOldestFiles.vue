<script setup lang="ts">
const model = defineModel<boolean>({ required: true })
const { t } = useI18n()
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.auto_delete_oldest_files.title')"
    :description="t('pages.profile.settings.auto_delete_oldest_files.description')"
  >
    <PInputSwitch v-model="model" />
  </HorizontalActionPanel>
</template>
