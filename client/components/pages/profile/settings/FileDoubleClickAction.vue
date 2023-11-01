<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: FileDoubleClickAction
}>()

const model = ref(initialValue)

const { t } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({ fileDoubleClickAction: value }))

const options = computed(() => [
  { icon: 'i-tabler-external-link', name: t('pages.profile.settings.file_double_click_action.open_new_tab'), value: 'OPEN_NEW_TAB' },
  { icon: 'i-tabler-eye', name: t('pages.profile.settings.file_double_click_action.open_preview'), value: 'OPEN_PREVIEW' },
  { icon: 'i-tabler-link', name: t('pages.profile.settings.file_double_click_action.copy_link'), value: 'COPY_LINK' },
  { icon: 'i-tabler-sparkles', name: t('pages.profile.settings.file_double_click_action.copy_custom_link'), value: 'COPY_CUSTOM_LINK' },
  { icon: 'i-tabler-pencil', name: t('pages.profile.settings.file_double_click_action.rename'), value: 'RENAME_FILE' },
  { icon: 'i-tabler-trash', name: t('pages.profile.settings.file_double_click_action.delete'), value: 'DELETE_FILE' },
  { icon: 'i-tabler-download', name: t('pages.profile.settings.file_double_click_action.download'), value: 'DOWNLOAD_FILE' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.file_double_click_action.title')"
    :description="t('pages.profile.settings.file_double_click_action.description')"
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
          <div class="text-truncate">
            {{ getOption(selectedOption)!.name }}
          </div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.file_double_click_action.choose') }}
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
