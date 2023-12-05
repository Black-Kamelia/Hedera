<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: boolean
}>()
const model = ref(initialValue)

const { t } = useI18n()
const { patchConfiguration, isError } = useConfigurationField(model, value => ({ enableRegistrations: value }))
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.configuration.general.toggle_registration.title')"
    :description="t('pages.configuration.general.toggle_registration.description')"
    :error="isError"
  >
    <PInputSwitch v-model="model" :class="{ 'p-invalid': isError }" @input="patchConfiguration" />
  </HorizontalActionPanel>
</template>
