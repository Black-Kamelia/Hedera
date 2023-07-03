<script setup lang="ts">
const model = defineModel<'en' | 'fr'>({ default: 'en', required: true })

const { t } = useI18n()

const options = computed(() => [
  { name: t('locale.fr'), value: 'en' },
  { name: t('locale.en'), value: 'fr' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.preferred_locale.title')"
    :description="t('pages.profile.settings.preferred_locale.description')"
  >
    <PDropdown
      v-model="model"
      :options="options"
      option-label="name"
      option-value="value"
      class="w-full md:w-14rem"
    >
      <template #value="{ value }">
        <div v-if="value" class="flex align-items-center">
          <!-- <i :class="`${getOption(value).icon} mr-2`" /> -->
          <div>{{ getOption(value).name }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.preferred_locale.choose') }}
        </span>
      </template>
      <template #option="slotProps">
        <div class="flex align-items-center">
          <i :class="`${slotProps.option.icon} mr-2`" />
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
