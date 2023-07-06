<script setup lang="ts">
interface Values {
  timeStyle: 'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'
  dateStyle: 'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'
}

const { value: initialValue } = defineProps<{
  value: Values
}>()
const model = ref(initialValue)

const { t, d } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({
  preferredDateStyle: value.dateStyle.toUpperCase(),
  preferredTimeStyle: value.timeStyle.toUpperCase(),
} as Partial<UserSettings>))
const now = useNow()

const options = computed(() => [
  { dateStyle: 'short', timeStyle: 'short' },
  { dateStyle: 'short', timeStyle: 'medium' },
  { dateStyle: 'short', timeStyle: 'long' },
  { dateStyle: 'short', timeStyle: 'full' },
  { dateStyle: 'medium', timeStyle: 'short' },
  { dateStyle: 'medium', timeStyle: 'medium' },
  { dateStyle: 'medium', timeStyle: 'long' },
  { dateStyle: 'medium', timeStyle: 'full' },
  { dateStyle: 'long', timeStyle: 'short' },
  { dateStyle: 'long', timeStyle: 'medium' },
  { dateStyle: 'long', timeStyle: 'long' },
  { dateStyle: 'long', timeStyle: 'full' },
  { dateStyle: 'full', timeStyle: 'short' },
  { dateStyle: 'full', timeStyle: 'medium' },
  { dateStyle: 'full', timeStyle: 'long' },
  { dateStyle: 'full', timeStyle: 'full' },
])
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.preferred_date_time_format.title')"
    :description="t('pages.profile.settings.preferred_date_time_format.description')"
    :error="isError"
  >
    <PDropdown
      v-model="model"
      :options="options"
      option-label="name"
      class="w-full md:w-14rem"
      :class="{ 'p-invalid': isError }"
      @update:model-value="patchSetting"
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
