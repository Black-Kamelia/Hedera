<script setup lang="ts">
const model = defineModel<'system' | 'light' | 'dark'>({ default: 'system', required: true })

const { t } = useI18n()

const options = computed(() => [
  { icon: 'i-tabler-device-desktop', name: t('theme.system'), value: 'system' },
  { icon: 'i-tabler-sun', name: t('theme.light'), value: 'light' },
  { icon: 'i-tabler-moon', name: t('theme.dark'), value: 'dark' },
])

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.preferred_theme.title')"
    :description="t('pages.profile.settings.preferred_theme.description')"
    local-setting
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
          <i :class="`${getOption(value).icon} mr-2`" />
          <div>{{ getOption(value).name }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.preferred_theme.choose') }}
        </span>
      </template>
      <template #option="slotProps">
        <div class="flex align-items-center">
          <i :class="`${slotProps.option.icon} mr-2`" />
          <div>{{ slotProps.option.name }}</div>
        </div>
      </template>
    </PDropdown>
  </HorizontalActionPanel>
</template>
