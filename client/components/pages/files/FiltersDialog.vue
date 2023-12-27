<script lang="ts" setup>
import { FileDeletedEvent } from '~/utils/events'

const visible = defineModel<boolean>('visible')

const { t } = useI18n()
const filters = useFilesFilters()
const localFilters = reactiveFilters(filters)

const visibilityOptions = [
  { name: t('pages.files.visibility.public'), value: 'PUBLIC', icon: 'i-tabler-world' },
  { name: t('pages.files.visibility.unlisted'), value: 'UNLISTED', icon: 'i-tabler-link' },
  { name: t('pages.files.visibility.private'), value: 'PRIVATE', icon: 'i-tabler-eye-off' },
]
const tokenPlaceholder = computed(() => {
  if (localFilters.noToken) {
    return t('pages.files.filters.no_token')
  } else {
    return t('pages.files.filters.tokens_unset')
  }
})

const { data: formats, pending: formatsPending, refresh } = useAsyncData(() => {
  return $fetchAPI<Array<string>>('/files/filters/formats', { method: 'GET' })
    .then(tokens => tokens?.map(type => ({ name: type })) ?? [])
})
const { data: tokens, pending: tokensPending } = useAsyncData(() => {
  return $fetchAPI<Array<PersonalTokenDTO>>('/files/filters/tokens', { method: 'GET' })
})

useEventBus(FileDeletedEvent).on(() => refresh())

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
        <p>{{ t('pages.files.table.visibility') }}</p>
        <PSelectButton
          v-model="localFilters.visibility"
          class="w-full"
          :options="visibilityOptions"
          option-label="name"
          option-value="value"
          multiple
          aria-labelledby="multiple"
          option-disabled="disabled"
          :pt="{
            root: { class: 'grid grid-cols-3' },
            button: { class: 'justify-center' },
          }"
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
        <p>{{ t('pages.files.table.creation_date') }}</p>
        <div class="flex flex-col sm:flex-row gap-3">
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
        <p>{{ t('pages.files.table.size') }}</p>
        <div class="flex flex-col sm:flex-row gap-3">
          <FileSizeInput v-model="localFilters.minimalSize" class="w-full" :pt="{ input: { class: 'min-h-3.125em w-full' } }" :placeholder="t('pages.files.filters.minimum_size')" />
          <FileSizeInput v-model="localFilters.maximalSize" class="w-full" :pt="{ input: { class: 'min-h-3.125em w-full' } }" :placeholder="t('pages.files.filters.maximum_size')" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <p>{{ t('pages.files.table.format') }}</p>
        <PMultiSelect
          v-model="localFilters.formats"
          :options="formats"
          option-label="name"
          option-value="name"
          :placeholder="t('pages.files.filters.all_formats')"
          class="min-w-0"
          filter
          :loading="formatsPending"
          :selected-items-label="t('pages.files.filters.formats')"
        >
          <template #value="slotOptions">
            <span v-if="formatsPending"><PSkeleton class="my-1" width="10rem" height="1rem" /></span>
            <span v-else-if="slotOptions.value.length === 0">{{ slotOptions.placeholder }}</span>
            <span v-else>{{ t('pages.files.filters.formats', { count: slotOptions.value.length }) }}</span>
          </template>
        </PMultiSelect>
      </div>

      <div class="flex flex-col gap-2">
        <p>{{ t('pages.files.table.token') }}</p>
        <PMultiSelect
          v-model="localFilters.tokens"
          :options="tokens"
          option-label="name"
          option-value="id"
          :placeholder="tokenPlaceholder"
          class="min-w-0"
          filter
          :loading="tokensPending"
          :selected-items-label="t('pages.files.filters.tokens')"
        >
          <template #value="slotOptions">
            <span v-if="tokensPending"><PSkeleton class="my-1" width="10rem" height="1rem" /></span>
            <span v-else-if="slotOptions.value.length === 0">{{ slotOptions.placeholder }}</span>
            <span v-else>{{ t('pages.files.filters.tokens', { count: slotOptions.value.length }) }}</span>
          </template>
          <template #option="slotOptions">
            <div class="flex flex-row gap-2 items-center">
              {{ slotOptions.option.name }}
              <span class="p-text-secondary">{{ t('pages.files.filters.tokens_files', { count: slotOptions.option.usage }) }}</span>
              <PTag v-if="slotOptions.option.deleted" :value="t('pages.files.filters.token_deleted')" severity="danger" rounded />
            </div>
          </template>
          <template #footer>
            <hr>
            <div class="p-5 flex flex-row gap-2">
              <PCheckbox v-model="localFilters.noToken" input-id="noToken" binary />
              <label for="noToken">{{ t('pages.files.filters.files_without_token') }}</label>
            </div>
          </template>
        </PMultiSelect>
      </div>
    </div>

    <template #footer>
      <PButton :label="t('pages.files.filters.reset')" icon="i-tabler-arrow-back-up" text @click="reset" />
      <PButton :label="t('pages.files.filters.apply')" icon="i-tabler-check" @click="applyAndClose" />
    </template>
  </PDialog>
</template>
