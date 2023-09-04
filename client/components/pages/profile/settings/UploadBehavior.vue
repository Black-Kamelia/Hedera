<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: UploadBehavior
}>()

const model = ref(initialValue)

const { t } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({ uploadBehavior: value }))

const options = computed(() => [
  { icon: 'i-tabler-bolt', name: t('pages.profile.settings.upload_behavior.instant'), value: 'INSTANT' },
  { icon: 'i-tabler-hand-click', name: t('pages.profile.settings.upload_behavior.manual'), value: 'MANUAL' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.upload_behavior.title')"
    :description="t('pages.profile.settings.upload_behavior.description')"
    :error="isError"
  >
    <PDropdown
      v-model="model"
      :options="options"
      option-label="name"
      option-value="value"
      class="w-full md:w-14rem"
      :class="{ 'p-invalid': isError }"
      @update:model-value="patchSetting"
    >
      <template #value="{ value: selectedOption }">
        <div v-if="selectedOption" class="flex items-center gap-2">
          <i :class="getOption(selectedOption)!.icon" />
          <div>{{ getOption(selectedOption)!.name }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.upload_behavior.choose') }}
        </span>
      </template>
      <template #option="slotProps">
        <div class="flex items-center gap-2">
          <i :class="slotProps.option.icon" />
          <div>{{ slotProps.option.name }}</div>
        </div>
      </template>
    </PDropdown>
  </HorizontalActionPanel>
</template>
