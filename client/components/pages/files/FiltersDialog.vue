<script lang="ts" setup>
const visible = defineModel<boolean>('visible')

const { t } = useI18n()

const visibilityOptions = [
  { name: t('pages.files.visibility.public'), value: 'PUBLIC', icon: 'i-tabler-world' },
  { name: t('pages.files.visibility.unlisted'), value: 'UNLISTED', icon: 'i-tabler-link' },
  { name: t('pages.files.visibility.protected'), value: 'PROTECTED', disabled: true, icon: 'i-tabler-lock' },
  { name: t('pages.files.visibility.private'), value: 'PRIVATE', icon: 'i-tabler-eye-off' },
]

const { data, pending } = useFetchAPI<Array<string>>('/files/formats', { method: 'GET' })
const formats = computed(() => data.value?.map(type => ({ name: type })) ?? [])

const filters = useFilesFilters()
const localFilters = reactiveFilters(filters)

function applyAndClose() {
  filters.updateFilters(localFilters)
  visible.value = false
}

function reset() {
  resetFilters(localFilters)
}

watch(visible, (visible) => {
  if (visible) {
    loadFilters(localFilters, filters)
  }
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    :header="t('pages.files.advanced_filters')"
    :draggable="false"
  >
    <div class="grid grid-cols-1 xl:grid-cols-2 grid-gap-6 justify-items-stretch">
      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.visibility') }}</h2>
        <PSelectButton
          v-model="localFilters.visibility" class="w-full" :options="visibilityOptions" option-label="name" option-value="value" multiple
          aria-labelledby="multiple" option-disabled="disabled"
        >
          <template #option="slotProps">
            <div class="flex flex-row gap-2">
              <i :class="slotProps.option.icon" />
              <span>{{ slotProps.option.name }}</span>
            </div>
          </template>
        </PSelectButton>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.creation_date') }}</h2>
        <div class="flex flex-row gap-3">
          <PCalendar
            v-model="localFilters.startingDate"
            class="w-full"
            :placeholder="t('pages.files.filters.start_date')"
            show-button-bar
            show-time
            show-seconds
            show-icon
            hour-format="24"
            :show-on-focus="false"
          >
            <template #dropdownicon>
              <i class="i-tabler-calendar-event" />
            </template>
          </PCalendar>
          <PCalendar
            v-model="localFilters.endingDate"
            class="w-full"
            :placeholder="t('pages.files.filters.end_date')"
            show-button-bar
            show-time
            show-seconds
            show-icon
            hour-format="24"
            :show-on-focus="false"
          >
            <template #dropdownicon>
              <i class="i-tabler-calendar-event" />
            </template>
          </PCalendar>
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.size') }}</h2>
        <div class="flex flex-row gap-3">
          <FileSizeInput v-model="localFilters.minimalSize" class="w-full" :pt="{ input: { class: 'w-full' } }" :placeholder="t('pages.files.filters.minimum_size')" />
          <FileSizeInput v-model="localFilters.maximalSize" class="w-full" :pt="{ input: { class: 'w-full' } }" :placeholder="t('pages.files.filters.maximum_size')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.views') }}</h2>
        <div class="flex flex-row gap-3">
          <PInputNumber v-model="localFilters.minimalViews" disabled class="w-full" :placeholder="t('pages.files.filters.minimal_views')" />
          <PInputNumber v-model="localFilters.maximalViews" disabled class="w-full" :placeholder="t('pages.files.filters.maximal_views')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.format') }}</h2>
        <PMultiSelect
          v-model="localFilters.formats"
          :options="formats"
          option-label="name"
          option-value="name"
          :placeholder="t('pages.files.filters.all_formats')"
          class="min-w-0"
          filter
          :loading="pending"
          :selected-items-label="t('pages.files.filters.formats')"
        >
          <template #value="slotOptions">
            <span v-if="pending"><PSkeleton class="my-1" width="10rem" height="1rem" /></span>
            <span v-else-if="slotOptions.value.length === 0">{{ slotOptions.placeholder }}</span>
            <span v-else>{{ t('pages.files.filters.formats', { count: slotOptions.value.length }) }}</span>
          </template>
        </PMultiSelect>
      </div>
    </div>

    <template #footer>
      <PButton :label="t('pages.files.filters.reset')" icon="i-tabler-arrow-back-up" text @click="reset" />
      <PButton :label="t('pages.files.filters.apply')" icon="i-tabler-check" autofocus @click="applyAndClose" />
    </template>
  </PDialog>
</template>
