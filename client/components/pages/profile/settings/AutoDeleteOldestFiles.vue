<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: boolean
}>()
const model = ref(initialValue)

const { t } = useI18n()
const { patchSettings } = useSettingsPage()

watch(model, (value) => {
  patchSettings({ autoRemoveFiles: value })
})
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.auto_delete_oldest_files.title')"
    :description="t('pages.profile.settings.auto_delete_oldest_files.description')"
  >
    <PInputSwitch v-model="model" />
  </HorizontalActionPanel>
</template>
