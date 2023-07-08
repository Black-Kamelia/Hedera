<script setup lang="ts">
interface Values {
  timeStyle: DateTimeStyle
  dateStyle: DateTimeStyle
}

const { value: initialValue } = defineProps<{
  value: Values
}>()
const model = ref(initialValue)

const { t, d } = useI18n()
const { patchSetting, isError } = useSetting(model, value => ({
  preferredDateStyle: value.dateStyle,
  preferredTimeStyle: value.timeStyle,
}))
const now = useNow()

const options = [
  { dateStyle: 'SHORT', timeStyle: 'SHORT' },
  { dateStyle: 'SHORT', timeStyle: 'MEDIUM' },
  { dateStyle: 'SHORT', timeStyle: 'LONG' },
  { dateStyle: 'SHORT', timeStyle: 'FULL' },
  { dateStyle: 'MEDIUM', timeStyle: 'SHORT' },
  { dateStyle: 'MEDIUM', timeStyle: 'MEDIUM' },
  { dateStyle: 'MEDIUM', timeStyle: 'LONG' },
  { dateStyle: 'MEDIUM', timeStyle: 'FULL' },
  { dateStyle: 'LONG', timeStyle: 'SHORT' },
  { dateStyle: 'LONG', timeStyle: 'MEDIUM' },
  { dateStyle: 'LONG', timeStyle: 'LONG' },
  { dateStyle: 'LONG', timeStyle: 'FULL' },
  { dateStyle: 'FULL', timeStyle: 'SHORT' },
  { dateStyle: 'FULL', timeStyle: 'MEDIUM' },
  { dateStyle: 'FULL', timeStyle: 'LONG' },
  { dateStyle: 'FULL', timeStyle: 'FULL' },
]
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
      <template #value="{ value: selectedOption }">
        <div v-if="selectedOption" class="flex align-items-center">
          <div>{{ d(now, selectedOption) }}</div>
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
