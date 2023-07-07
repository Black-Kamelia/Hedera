<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: 'en' | 'fr'
}>()

const model = ref(initialValue)

const { t } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({ preferredLocale: value }))

const options = computed(() => [
  { name: t('locale.en'), flag: '/assets/img/flags/en_US.svg', value: 'en' },
  { name: t('locale.fr'), flag: '/assets/img/flags/fr_FR.svg', value: 'fr' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.preferred_locale.title')"
    :description="t('pages.profile.settings.preferred_locale.description')"
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
          <img :src="getOption(value).flag" class="w-5" alt="Flag icon">
          <div>{{ getOption(value).name }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.preferred_locale.choose') }}
        </span>
      </template>
      <template #option="slotProps">
        <div class="flex items-center gap-2">
          <img :src="slotProps.option.flag" class="w-5" alt="Flag icon">
          <div>{{ slotProps.option.name }}</div>
        </div>
      </template>
    </PDropdown>

    <template #cta>
      <PButton
        :label="t('pages.profile.settings.contribute_to_translation')"
        icon-pos="right"
        icon="i-tabler-arrow-narrow-right"
        size="small"
        text
      />
    </template>
  </HorizontalActionPanel>
</template>
