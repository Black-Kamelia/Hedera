<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: boolean
}>()
const model = ref(initialValue)

const { t } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({ autoRemoveFiles: value }))
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.auto_delete_oldest_files.title')"
    :description="t('pages.profile.settings.auto_delete_oldest_files.description')"
    :error="isError"
  >
    <PInputSwitch v-model="model" :class="{ 'p-invalid': isError }" @input="patchSetting" />
  </HorizontalActionPanel>
</template>
