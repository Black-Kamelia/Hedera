<script lang="ts" setup>
import type {
  DataTablePageEvent,
  DataTableRowContextMenuEvent,
  DataTableRowDoubleClickEvent,
  DataTableSortEvent, DataTableSortMeta,
} from 'primevue/datatable'
import type { PContextMenu } from '#components'

const DEFAULT_PAGE = 0
const DEFAULT_PAGE_SIZE = 25

const { locale, t, d } = useI18n()
const filters = useFilesFilters()

const selectedRows = defineModel<Array<FileRepresentationDTO>>('selectedRows', { default: () => [] })
const selectedRow = ref<Nullable<FileRepresentationDTO>>(null)
const sort = ref<DataTableSortMeta[]>([{ field: 'createdAt', order: -1 }])
const query = defineModel<string>('query', { default: () => '' })
const page = ref(DEFAULT_PAGE)
const pageSize = ref(DEFAULT_PAGE_SIZE)
const sortDefinition = ref<SorterDefinitionDTO>([])
const filterDefinition = computed(() => filtersToDefinition(filters, query.value))

const pageDefinition = computed<PageDefinitionDTO>(() => ({ sorter: sortDefinition.value, filters: filterDefinition.value }))
const { data, pending, error, refresh } = useLazyFetchAPI<PageableDTO>('/files/search', {
  method: 'POST',
  body: pageDefinition,
  query: { page, pageSize },
})
const debouncedPending = useDebounce(pending, 500)

const files = computed(() => data.value?.page.items ?? [])
const rows = computed(() => data.value?.page.pageSize ?? pageSize.value)
const totalRecords = computed(() => data.value?.page.totalItems ?? 0)
const selectedRowId = computed(() => selectedRow.value?.id)
const loading = computed(() => pending.value && debouncedPending.value)

function updateSelectedRow(newRow: FileRepresentationDTO) {
  const file = files.value.find((f: FileRepresentationDTO) => f.id === selectedRowId.value)
  if (file && newRow) {
    Object.assign(file, newRow)
  }
}

function unselectRow() {
  selectedRow.value = null
}

provide(FileTableKey, {
  selectedRow,
  selectedRowId,
  updateSelectedRow,
  unselectRow,
  refresh,
})

const contextMenu = ref<InstanceType<typeof PContextMenu> | null>(null)
provide(FileTableContextMenuKey, contextMenu)

function resetPage() {
  page.value = DEFAULT_PAGE
  pageSize.value = DEFAULT_PAGE_SIZE
  sortDefinition.value = []
  refresh()
}

function onPage(event: DataTablePageEvent) {
  page.value = event.page
  pageSize.value = event.rows
}

function onSort(event: DataTableSortEvent) {
  if (!event.multiSortMeta) return

  sortDefinition.value = event.multiSortMeta.map(({ field, order }) => ({
    field,
    direction: order === 1 ? 'ASC' : 'DESC',
  }))
}

function onRowContextMenu(event: DataTableRowContextMenuEvent) {
  contextMenu.value?.show(event.originalEvent)
}

function onRowDoubleClick(event: DataTableRowDoubleClickEvent) {
  window.open(`/${event.data.code}`)
}
</script>

<template>
  <FilesTableContextMenu />

  <div v-if="error" class="h-full w-full flex flex-col justify-center items-center">
    <!-- TODO: Error state illustration -->
    <img class="w-10em" src="/assets/img/new_file.png" alt="Error file">
    <h1 class="text-2xl">
      {{ t('pages.files.error.title') }}
    </h1>
    <p class="pb-10">
      {{ t('pages.files.error.description') }}
    </p>
    <PButton
      v-if="error.statusCode === 400"
      :loading="pending"
      rounded
      :label="t('pages.files.error.reset_button')"
      @click="resetPage()"
    />
    <PButton v-else :loading="pending" rounded :label="t('pages.files.error.retry_button')" @click="refresh()" />
  </div>

  <div v-else-if="files.length === 0 && !pending && !filters.isEmpty" class="h-full w-full flex flex-col justify-center items-center">
    <!-- TODO: Empty state illustration -->
    <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
    <h1 class="text-2xl">
      {{ t('pages.files.no_results.title') }}
    </h1>
    <p class="pb-10">
      {{ t('pages.files.no_results.description') }}
    </p>
    <PButton rounded :label="t('pages.files.no_results.reset_filters')" @click="filters.reset()" />
  </div>

  <div v-else-if="files.length === 0 && !pending" class="h-full w-full flex flex-col justify-center items-center">
    <!-- TODO: Empty state illustration -->
    <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
    <h1 class="text-2xl">
      {{ t('pages.files.empty.title') }}
    </h1>
    <p class="pb-10">
      {{ t('pages.files.empty.description') }}
    </p>
    <PButton rounded :label="t('pages.files.empty.upload_button')" @click="navigateTo('/upload')" />
  </div>

  <PDataTable
    v-else
    v-model:selection="selectedRows"
    v-model:contextMenuSelection="selectedRow"
    v-model:multi-sort-meta="sort"
    class="h-full"
    data-key="id"
    lazy
    :value="loading ? Array.from({ length: rows }) : files"
    scrollable
    scroll-height="flex"
    paginator
    paginator-template="FirstPageLink PrevPageLink CurrentPageReport NextPageLink LastPageLink RowsPerPageDropdown"
    :current-page-report-template="t('pages.files.table.paginator', {}, { escapeParameter: true })"
    :rows-per-page-options="[10, 25, 50, 100]"
    :rows="rows"
    :total-records="totalRecords"
    sort-mode="multiple"
    removable-sort
    context-menu
    @page="onPage"
    @sort="onSort"
    @row-contextmenu="onRowContextMenu"
    @row-dblclick="onRowDoubleClick"
  >
    <PColumn style="width: 3.375em;" selection-mode="multiple" />

    <PColumn style="width: 6em;" field="code" :header="t('pages.files.table.preview')" :sortable="false">
      <template #body="slotProps">
        <Transition v-if="slotProps.data" name="fade" mode="out-in">
          <MediaPreview :key="slotProps.data.mimeType" :data="slotProps.data" />
        </Transition>
        <PSkeleton v-else width="6rem" height="4rem" />
      </template>
    </PColumn>

    <PColumn
      style="max-width: 30em; text-overflow: ellipsis; overflow: hidden;"
      field="name"
      sortable
      :header="t('pages.files.table.name')"
    >
      <template #sorticon="slotProps">
        <i
          class="ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <Transition v-if="slotProps.data" name="fade" mode="out-in">
          <span :key="slotProps.data.name" class="text-nowrap">{{ slotProps.data.name }}</span>
        </Transition>
        <PSkeleton v-else width="10rem" height="1rem" />
      </template>
    </PColumn>

    <PColumn field="size" sortable :header="t('pages.files.table.size')">
      <template #sorticon="slotProps">
        <i
          class="ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <span v-if="slotProps.data">{{ humanSizeStructure(slotProps.data.size, locale, t) }}</span>
        <PSkeleton v-else width="5rem" height="1rem" />
      </template>
    </PColumn>

    <PColumn
      style="max-width: 10em; text-overflow: ellipsis; overflow: hidden;"
      field="mimeType"
      sortable
      :header="t('pages.files.table.format')"
    >
      <template #sorticon="slotProps">
        <i
          class="ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <Transition v-if="slotProps.data" name="fade" mode="out-in">
          <span :key="slotProps.data.mimeType">{{ slotProps.data.mimeType }}</span>
        </Transition>
        <PSkeleton v-else width="5rem" height="1rem" />
      </template>
    </PColumn>

    <PColumn field="visibility" sortable :header="t('pages.files.table.visibility')">
      <template #sorticon="slotProps">
        <i
          class="ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <Transition v-if="slotProps.data" name="fade" mode="out-in">
          <VisibilityDisplayer :key="slotProps.data.visibility" :visibility="slotProps.data.visibility" />
        </Transition>
        <div v-else class="flex flex-row items-center gap-2">
          <PSkeleton size="1.5rem" shape="circle" />
          <PSkeleton width="5rem" height="1rem" />
        </div>
      </template>
    </PColumn>

    <PColumn field="createdAt" sortable :header="t('pages.files.table.creation_date')">
      <template #sorticon="slotProps">
        <i
          class="ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <span v-if="slotProps.data">{{ d(slotProps.data.createdAt) }}</span>
        <PSkeleton v-else width="8rem" height="1rem" />
      </template>
    </PColumn>
  </PDataTable>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
