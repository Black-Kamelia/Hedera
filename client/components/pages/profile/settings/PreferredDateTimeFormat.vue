<script setup lang="ts">
const model = defineModel<{
  dateStyle: 'short' | 'medium' | 'long'
  timeStyle: 'short' | 'medium' | 'long'
}>({ default: { dateStyle: 'short', timeStyle: 'medium' }, required: true })

const { t, d } = useI18n()
const now = useNow()

const options = computed(() => [
  { dateStyle: 'short', timeStyle: 'short' },
  { dateStyle: 'short', timeStyle: 'medium' },
  { dateStyle: 'short', timeStyle: 'long' },
  { dateStyle: 'medium', timeStyle: 'short' },
  { dateStyle: 'medium', timeStyle: 'medium' },
  { dateStyle: 'medium', timeStyle: 'long' },
  { dateStyle: 'long', timeStyle: 'short' },
  { dateStyle: 'long', timeStyle: 'medium' },
  { dateStyle: 'long', timeStyle: 'long' },
])
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.preferred_date_time_format.title')"
    :description="t('pages.profile.settings.preferred_date_time_format.description')"
  >
    <PDropdown
      v-model="model"
      :options="options"
      option-label="name"
      class="w-full md:w-14rem"
    >
      <template #value="{ value }">
        <div v-if="value" class="flex align-items-center">
          <div>{{ d(now, value) }}</div>
        </div>
        <span v-else>
          {{ t('pages.profile.settings.preferred_theme.choose') }}
        </span>
      </template>
      <template #option="{ option }">
        <div class="flex align-items-center">
          <div>{{ d(now, option) }}</div>
        </div>
      </template>
    </PDropdown>
  </HorizontalActionPanel>
</template>
