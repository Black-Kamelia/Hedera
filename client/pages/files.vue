<script lang="ts" setup>
const { t } = useI18n()
const filters = useFilesFilters()

usePageName(() => t('pages.files.title'))
definePageMeta({ layout: 'main', middleware: ['auth'] })

const openFiltersDialog = ref(false)
const searchQuery = ref('')
</script>

<template>
  <div class="py-4 px-4 sm:px-8 h-full flex flex-col gap-4">
    <div class="flex flex-col sm:flex-row gap-2 sm:gap-4">
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
      <FilesTable v-model:query="searchQuery" />
    </div>

    <FiltersDialog v-model:visible="openFiltersDialog" />
  </div>
</template>
