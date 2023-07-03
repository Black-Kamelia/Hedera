<script setup lang="ts">
const model = defineModel<boolean>({ required: true })
const { t } = useI18n()
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.enable_animations.title')"
    :description="t('pages.profile.settings.enable_animations.description')"
  >
    <PInputSwitch v-model="model" />
  </HorizontalActionPanel>
</template>
