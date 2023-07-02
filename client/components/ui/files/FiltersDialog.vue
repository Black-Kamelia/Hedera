<script lang="ts" setup>
import { useFilesFilters } from '~/stores/useFilesFilters'

const visible = defineModel<boolean>('visible')

const { t } = useI18n()

const visibilityOptions = ref([
  { name: t('pages.files.visibility.public'), value: 'PUBLIC', icon: 'i-tabler-world' },
  { name: t('pages.files.visibility.unlisted'), value: 'UNLISTED', icon: 'i-tabler-link' },
  { name: t('pages.files.visibility.protected'), value: 'PROTECTED', disabled: true, icon: 'i-tabler-lock' },
  { name: t('pages.files.visibility.private'), value: 'PRIVATE', icon: 'i-tabler-eye-off' },
])
const types = ref([
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
])

const filters = useFilesFilters()

const visibility = ref<Array<string>>(filters.visibility.value)
const startingDate = ref<Date | null>(filters.startingDate.value)
const endingDate = ref<Date | null>(filters.endingDate.value)
const minimalSize = ref<FileSize | null>(filters.minimalSize.value)
const maximalSize = ref<FileSize | null>(filters.maximalSize.value)
const minimalViews = ref<number | null>(filters.minimalViews.value)
const maximalViews = ref<number | null>(filters.maximalViews.value)
const formats = ref<Array<string>>(filters.formats.value)
const owners = ref<Array<string>>(filters.owners.value)

function applyAndClose() {
  filters.visibility.value = visibility.value
  filters.startingDate.value = startingDate.value
  filters.endingDate.value = endingDate.value
  filters.minimalSize.value = minimalSize.value
  filters.maximalSize.value = maximalSize.value
  filters.minimalViews.value = minimalViews.value
  filters.maximalViews.value = maximalViews.value
  filters.formats.value = formats.value
  filters.owners.value = owners.value

  visible.value = false
}

function reset() {
  visibility.value = []
  startingDate.value = null
  endingDate.value = null
  minimalSize.value = null
  maximalSize.value = null
  minimalViews.value = null
  maximalViews.value = null
  formats.value = []
  owners.value = []
}

watch(visible, () => {
  if (visible.value) {
    visibility.value = filters.visibility.value
    startingDate.value = filters.startingDate.value
    endingDate.value = filters.endingDate.value
    minimalSize.value = filters.minimalSize.value
    maximalSize.value = filters.maximalSize.value
    minimalViews.value = filters.minimalViews.value
    maximalViews.value = filters.maximalViews.value
    formats.value = filters.formats.value
    owners.value = filters.owners.value
  }
})
</script>

<template>
  <PDialog v-model:visible="visible" modal :header="t('pages.files.advanced_filters')" :draggable="false">
    <div class="grid grid-cols-1 xl:grid-cols-2 grid-gap-6 justify-items-stretch">
      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.visibility') }}</h2>
        <PSelectButton
          v-model="visibility" class="w-full" :options="visibilityOptions" option-label="name" option-value="value" multiple
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
            v-model="startingDate" class="w-full" :placeholder="t('pages.files.filters.start_date')" show-button-bar show-time
            hour-format="24"
          />
          <PCalendar
            v-model="endingDate" class="w-full" :placeholder="t('pages.files.filters.end_date')" show-button-bar show-time
            hour-format="24"
          />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.size') }}</h2>
        <div class="flex flex-row gap-3">
          <FileSizeInput v-model="minimalSize" class="w-full" :pt="{ input: { class: 'w-full' } }" :placeholder="t('pages.files.filters.minimum_size')" />
          <FileSizeInput v-model="maximalSize" class="w-full" :pt="{ input: { class: 'w-full' } }" :placeholder="t('pages.files.filters.maximum_size')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.views') }}</h2>
        <div class="flex flex-row gap-3">
          <PInputNumber v-model="minimalViews" class="w-full" :placeholder="t('pages.files.filters.minimal_views')" />
          <PInputNumber v-model="maximalViews" class="w-full" :placeholder="t('pages.files.filters.maximal_views')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.format') }}</h2>
        <PMultiSelect
          v-model="formats" :options="types" option-label="name" option-group-label="name" option-group-children="items" :placeholder="t('pages.files.filters.all_formats')"
          :max-selected-labels="3" class="w-full" filter :selected-items-label="t('pages.files.filters.formats')"
        />
      </div>

      <div class="flex flex-col gap-2">
        <h2>{{ t('pages.files.table.owner') }}</h2>
        <PMultiSelect
          v-model="owners" :options="types" option-label="name" :placeholder="t('pages.files.filters.all_owners')"
          :max-selected-labels="3" class="w-full" :selected-items-label="t('pages.files.filters.owners')"
        />
      </div>
    </div>

    <template #footer>
      <PButton :label="t('pages.files.filters.reset')" icon="i-tabler-arrow-back-up" text @click="reset()" />
      <PButton :label="t('pages.files.filters.apply')" icon="i-tabler-check" autofocus @click="applyAndClose()" />
    </template>
  </PDialog>
</template>
