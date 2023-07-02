<script lang="ts" setup>
const visible = defineModel<boolean>('visible')

const { t } = useI18n()

const visibilityOptions = [
  { name: t('pages.files.visibility.public'), value: 'PUBLIC', icon: 'i-tabler-world' },
  { name: t('pages.files.visibility.unlisted'), value: 'UNLISTED', icon: 'i-tabler-link' },
  { name: t('pages.files.visibility.protected'), value: 'PROTECTED', disabled: true, icon: 'i-tabler-lock' },
  { name: t('pages.files.visibility.private'), value: 'PRIVATE', icon: 'i-tabler-eye-off' },
]
const types = [
  {
    name: 'Images',
    items: [
      { name: 'image/png' },
      { name: 'image/jpg' },
      { name: 'image/gif' },
    ],
  },
  {
    name: 'Vidéos',
    items: [
      { name: 'video/mp4' },
      { name: 'video/avi' },
      { name: 'video/mkv' },
    ],
  },
  {
    name: 'Documents',
    items: [
      { name: 'application/pdf' },
    ],
  },
  {
    name: 'Musiques',
    items: [
      { name: 'audio/mp3' },
      { name: 'audio/wav' },
      { name: 'audio/ogg' },
    ],
  },
  {
    name: 'Archives',
    items: [
      { name: 'application/zip' },
      { name: 'application/x-rar-compressed' },
    ],
  },
]

const filters = useFilesFilters()

const localFilters = reactive({
  visibility: filters.visibility.value,
  startingDate: filters.startingDate.value,
  endingDate: filters.endingDate.value,
  minimalSize: filters.minimalSize.value,
  maximalSize: filters.maximalSize.value,
  minimalViews: filters.minimalViews.value,
  maximalViews: filters.maximalViews.value,
  formats: filters.formats.value,
  owners: filters.owners.value,
  reset() {
    this.visibility = []
    this.startingDate = null
    this.endingDate = null
    this.minimalSize = null
    this.maximalSize = null
    this.minimalViews = null
    this.maximalViews = null
    this.formats = []
    this.owners = []
  },
})

function applyAndClose() {
  filters.visibility.value = localFilters.visibility
  filters.startingDate.value = localFilters.startingDate
  filters.endingDate.value = localFilters.endingDate
  filters.minimalSize.value = localFilters.minimalSize
  filters.maximalSize.value = localFilters.maximalSize
  filters.minimalViews.value = localFilters.minimalViews
  filters.maximalViews.value = localFilters.maximalViews
  filters.formats.value = localFilters.formats
  filters.owners.value = localFilters.owners

  visible.value = false
}

watch(visible, () => {
  if (visible.value) {
    localFilters.visibility = filters.visibility.value
    localFilters.startingDate = filters.startingDate.value
    localFilters.endingDate = filters.endingDate.value
    localFilters.minimalSize = filters.minimalSize.value
    localFilters.maximalSize = filters.maximalSize.value
    localFilters.minimalViews = filters.minimalViews.value
    localFilters.maximalViews = filters.maximalViews.value
    localFilters.formats = filters.formats.value
    localFilters.owners = filters.owners.value
  }
})
</script>

<template>
  <PDialog v-model:visible="visible" modal :header="t('pages.files.advanced_filters')" :draggable="false">
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
            v-model="localFilters.startingDate" class="w-full" :placeholder="t('pages.files.filters.start_date')" show-button-bar show-time
            hour-format="24"
          />
          <PCalendar
            v-model="localFilters.endingDate" class="w-full" :placeholder="t('pages.files.filters.end_date')" show-button-bar show-time
            hour-format="24"
          />
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
          <PInputNumber v-model="localFilters.minimalViews" class="w-full" :placeholder="t('pages.files.filters.minimal_views')" />
          <PInputNumber v-model="localFilters.maximalViews" class="w-full" :placeholder="t('pages.files.filters.maximal_views')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.format') }}</h2>
        <PMultiSelect
          v-model="localFilters.formats" :options="types" option-label="name" option-group-label="name" option-group-children="items" :placeholder="t('pages.files.filters.all_formats')"
          :max-selected-labels="3" class="w-full" filter :selected-items-label="t('pages.files.filters.formats')"
        />
      </div>
    </div>

    <template #footer>
      <PButton :label="t('pages.files.filters.reset')" icon="i-tabler-arrow-back-up" text @click="localFilters.reset()" />
      <PButton :label="t('pages.files.filters.apply')" icon="i-tabler-check" autofocus @click="applyAndClose()" />
    </template>
  </PDialog>
</template>
