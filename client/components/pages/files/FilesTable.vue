<script lang="ts" setup>
import type {
  DataTablePageEvent,
  DataTableRowContextMenuEvent,
  DataTableRowDoubleClickEvent,
  DataTableSortEvent,
} from 'primevue/datatable'
import type { PContextMenu } from '#components'

const { locale, t, d } = useI18n()

const selectedRows = defineModel<Array<FileRepresentationDTO>>('selectedRows', { default: () => [] })
const selectedRow = ref<Nullable<FileRepresentationDTO>>(null)
const page = ref(0)
const pageSize = ref(10)

const { data, pending } = useLazyFetchAPI<PageableDTO>('/files/search', {
  method: 'POST',
  body: {},
  query: { page, pageSize },
})

const files = computed(() => data.value?.page.items ?? [])
const rows = computed(() => data.value?.page.pageSize ?? pageSize.value)
const totalRecords = computed(() => data.value?.page.totalItems ?? 0)
const selectedRowId = computed(() => selectedRow.value?.id)

function updateSelectedRow(newRow: FileRepresentationDTO) {
  const file = files.value.find((f: FileRepresentationDTO) => f.id === selectedRowId.value)
  if (file && newRow) {
    Object.assign(file, newRow)
  }
}

function removeSelectedRow() {
  // files.value = files.value.filter((f: FileRepresentationDTO) => f.id !== selectedRowId.value)
}

function unselectRow() {
  selectedRow.value = null
}

provide(FileTableKey, {
  selectedRow,
  selectedRowId,
  updateSelectedRow,
  removeSelectedRow,
  unselectRow,
})

const contextMenu = ref<InstanceType<typeof PContextMenu> | null>(null)
provide(FileTableContextMenuKey, contextMenu)

function onPage(event: DataTablePageEvent) {
  page.value = event.page
  pageSize.value = event.rows
}

function onSort(event: DataTableSortEvent) {

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

  <div v-if="files.length === 0" class="h-full w-full flex flex-col justify-center items-center">
    <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
    <h1 class="text-2xl">
      {{ t('pages.files.empty.title') }}
    </h1>
    <p class="pb-10">
      {{ t('pages.files.empty.description') }}
    </p>
    <PButton rounded :label="t('pages.files.empty.upload_button')" />
  </div>

  <PDataTable
    v-else
    v-model:selection="selectedRows"
    v-model:contextMenuSelection="selectedRow"
    class="h-full"
    data-key="id"
    lazy
    :value="files"
    :loading="pending"
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
        <Transition name="fade" mode="out-in">
          <MediaPreview :key="slotProps.data.mimeType" :data="slotProps.data" />
        </Transition>
      </template>
      <template #loading>
        <PSkeleton width="6rem" height="4rem" />
      </template>
    </PColumn>

    <PColumn field="name" sortable :header="t('pages.files.table.name')">
      <template #sorticon="slotProps">
        <i
          class="pointer-events-none ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <div class="flex flex-row gap-1 items-center justify-between">
          <div class="flex flex-col gap-1">
            <Transition name="fade" mode="out-in">
              <span :key="slotProps.data.name">{{ slotProps.data.name }}</span>
            </Transition>
            <!-- For future use -->
            <!-- <div class="flex flex-row items-center gap-1">
                <i class="i-tabler-eye text-xs" />
                <span class="text-xs">{{ 0 }}</span>
              </div> -->
          </div>
          <!-- For future use -->
          <!-- <PButton icon="i-tabler-star" severity="warning" rounded text /> -->
        </div>
      </template>
      <template #loading>
        <div class="flex flex-col gap-1">
          <PSkeleton width="10rem" height="1rem" />
          <PSkeleton width="2rem" height=".75rem" />
        </div>
      </template>
    </PColumn>

    <PColumn field="size" sortable :header="t('pages.files.table.size')">
      <template #sorticon="slotProps">
        <i
          class="pointer-events-none ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        {{ humanSizeStructure(slotProps.data.size, locale, t) }}
      </template>
      <template #loading>
        <PSkeleton width="5rem" height="1rem" />
      </template>
    </PColumn>

    <PColumn field="mimeType" sortable :header="t('pages.files.table.format')">
      <template #sorticon="slotProps">
        <i
          class="pointer-events-none ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <Transition name="fade" mode="out-in">
          <span :key="slotProps.data.mimeType">{{ slotProps.data.mimeType }}</span>
        </Transition>
      </template>
      <template #loading>
        <PSkeleton width="5rem" height="1rem" />
      </template>
    </PColumn>

    <PColumn field="visibility" sortable :header="t('pages.files.table.visibility')">
      <template #sorticon="slotProps">
        <i
          class="pointer-events-none ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        <Transition name="fade" mode="out-in">
          <VisibilityDisplayer :key="slotProps.data.visibility" :visibility="slotProps.data.visibility" />
        </Transition>
      </template>
      <template #loading>
        <div class="flex flex-row items-center gap-2">
          <PSkeleton size="1.5rem" shape="circle" />
          <PSkeleton width="5rem" height="1rem" />
        </div>
      </template>
    </PColumn>

    <PColumn field="creationDate" sortable :header="t('pages.files.table.creation_date')">
      <template #sorticon="slotProps">
        <i
          class="pointer-events-none ml-1 text-xs block" :class="{
            'i-tabler-arrows-sort': !slotProps.sorted,
            'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
            'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
          }"
        />
      </template>
      <template #body="slotProps">
        {{ d(slotProps.data.creationDate) }}
      </template>
      <template #loading>
        <PSkeleton width="8rem" height="1rem" />
      </template>
    </PColumn>

    <template #empty>
      <div class="flex-grow w-full flex flex-col justify-center items-center">
        <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
        <h1 class="text-2xl">
          {{ t('pages.files.empty.title') }}
        </h1>
        <p class="pb-10">
          {{ t('pages.files.empty.description') }}
        </p>
        <PButton rounded :label="t('pages.files.empty.upload_button')" />
      </div>
    </template>
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
