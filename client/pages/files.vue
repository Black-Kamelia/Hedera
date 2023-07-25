<script lang="ts" setup>
const { t } = useI18n()
const filters = useFilesFilters()

usePageName(() => t('pages.files.title'))
definePageMeta({ layout: 'sidebar', middleware: ['auth'] })

const openFiltersDialog = ref(false)
const searchQuery = ref('')

const selectedRows = ref<Array<FileRepresentationDTO>>([])
const selecting = computed(() => selectedRows.value.length > 0)
</script>

<template>
  <div class="py-4 px-8 h-full flex flex-col gap-4">
    <div class="flex flex-row gap-4">
      <span class="flex-grow p-input-icon-left">
        <i class="i-tabler-search" />
        <PInputText v-model="searchQuery" class="w-full p-inputtext-lg" :placeholder="t('pages.files.search_by_name')" />
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
      <FilesTable v-model:selectedRows="selectedRows" v-model:query="searchQuery" />
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
