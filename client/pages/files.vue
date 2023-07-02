<script lang="ts" setup>
const { t } = useI18n()
const filters = useFilesFilters()

usePageName(() => t('pages.files.title'))
definePageMeta({ layout: 'sidebar', middleware: ['auth'] })

const openFiltersDialog = ref(false)

const files = ref<Array<FileRepresentationDTO>>([])
const selectedRows = ref<Array<FileRepresentationDTO>>([])
const selecting = computed(() => selectedRows.value.length > 0)

const { data, isFinished, isLoading } = useAPI<PageableDTO>('/files/paged')
watch(isFinished, (isFinished) => {
  if (isFinished && data.value?.page.items)
    files.value = data.value?.page.items
})
</script>

<template>
  <div class="h-full flex flex-col gap-4">
    <div class="flex flex-row gap-4">
      <span class="flex-grow p-input-icon-left">
        <i class="i-tabler-search" />
        <PInputText class="w-full p-inputtext-lg" :placeholder="t('pages.files.search_by_name')" />
      </span>
      <PButton
        icon="i-tabler-filter"
        :label="t('pages.files.advanced_filters')"
        :outlined="filters.isEmpty"
        :badge="filters.isEmpty ? undefined : String(filters.activeFilters)"
        @click="openFiltersDialog = true"
      />
    </div>

    <div class="p-card p-0 overflow-hidden flex-grow">
      <div v-if="files.length === 0 && !isLoading" class="h-full w-full flex flex-col justify-center items-center">
        <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
        <h1 class="text-2xl">
          {{ t('pages.files.empty.title') }}
        </h1>
        <p class="pb-10">
          {{ t('pages.files.empty.description') }}
        </p>
        <PButton rounded :label="t('pages.files.empty.upload_button')" />
      </div>
      <FilesTable
        v-else
        v-model:selectedRows="selectedRows"
        v-model:files="files"
      />
    </div>

    <ActionButtons
      :selecting="selecting"
      @download="console.log('Download')"
      @change-visibility="console.log('Change visibility')"
      @unselect="console.log('Unselect')"
      @delete="console.log('Delete')"
    />

    <FiltersDialog v-model:visible="openFiltersDialog" />
  </div>
</template>
