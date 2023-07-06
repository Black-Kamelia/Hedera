<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: 'PUBLIC' | 'UNLISTED' | 'PRIVATE'
}>()

const model = ref(initialValue)

const { t } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({ defaultFileVisibility: value }))

const options = computed(() => [
  { icon: 'i-tabler-world', name: t('pages.files.visibility.public'), value: 'PUBLIC' },
  { icon: 'i-tabler-link', name: t('pages.files.visibility.unlisted'), value: 'UNLISTED' },
  { icon: 'i-tabler-eye-off', name: t('pages.files.visibility.private'), value: 'PRIVATE' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.default_files_visibility.title')"
    :description="t('pages.profile.settings.default_files_visibility.description')"
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
      <template #value="{ value }">
        <div v-if="value" class="flex items-center gap-2">
          <i :class="getOption(value).icon" />
          <div>{{ getOption(value).name }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.default_files_visibility.choose') }}
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
