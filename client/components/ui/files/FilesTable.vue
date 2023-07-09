<script lang="ts" setup>
import type { DataTableRowContextMenuEvent, DataTableRowDoubleClickEvent } from 'primevue/datatable'
import type { VirtualScrollerProps } from 'primevue/virtualscroller'
import type { PContextMenu } from '#components'

const emit = defineEmits<{
  (event: 'fetchMore', tableEvent: any): void
}>()

const { locale, t, d } = useI18n()

const files = defineModel<FileRepresentationDTO[]>('files', { required: true })
const selectedRows = defineModel<Array<FileRepresentationDTO>>('selectedRows', { default: () => [] })

const selectedRow = ref<Nullable<FileRepresentationDTO>>(null)
const selectedRowId = computed(() => selectedRow.value?.id)

function updateSelectedRow(newRow: FileRepresentationDTO) {
  const file = files.value.find((f: FileRepresentationDTO) => f.id === selectedRowId.value)
  if (file && newRow)
    Object.assign(file, newRow)
}

function removeSelectedRow() {
  files.value = files.value.filter((f: FileRepresentationDTO) => f.id !== selectedRowId.value)
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

function onRowContextMenu(event: DataTableRowContextMenuEvent) {
  contextMenu.value?.show(event.originalEvent)
}

function onRowDoubleClick(event: DataTableRowDoubleClickEvent) {
  window.open(`/${event.data.code}`)
}

const lazyLoading = ref(false)
const lazyContent = Array.from({ length: 3000 })
const lazyScrollerOptions: VirtualScrollerProps = {
  lazy: true,
  onLazyLoad: event => console.log(event),
  itemSize: (6 * 16) + 1,
  // delay: 200,
  showLoader: true,
  loading: lazyLoading.value,
  numToleratedItems: 25,
}

onMounted(() => {
  console.log(lazyContent)
  lazyLoading.value = true
})
</script>

<template>
  <FilesTableContextMenu />
  <PDataTable
    v-model:selection="selectedRows"
    v-model:contextMenuSelection="selectedRow"
    :value="lazyContent"
    data-key="id"
    scrollable
    scroll-height="flex"
    sort-mode="multiple"
    removable-sort
    context-menu
    class="h-full"
    :virtual-scroller-options="lazyScrollerOptions"
    @row-contextmenu="onRowContextMenu"
    @row-dblclick="onRowDoubleClick"
  >
    <PColumn style="width: 3.375em;" selection-mode="multiple">
      <template #loading />
    </PColumn>

    <PColumn style="width: 6em;" field="code" :header="t('pages.files.table.preview')" :sortable="false">
      <template #body="slotProps">
        <Transition name="fade" mode="out-in">
          <MediaPreview v-if="slotProps.data" :key="slotProps.data.mimeType" :data="slotProps.data" />
          <PSkeleton v-else width="6rem" height="4rem" />
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
              <span v-if="slotProps.data" :key="slotProps.data.name">{{ slotProps.data.name }}</span>
              <PSkeleton v-else width="10rem" height="1rem" />
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
          <!-- <PSkeleton width="2rem" height=".75rem" /> -->
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
        <span v-if="slotProps.data" :key="slotProps.data.size">{{ humanSizeStructure(slotProps.data?.size, locale, t) }}</span>
        <PSkeleton v-else width="5rem" height="1rem" />
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
          <span v-if="slotProps.data" :key="slotProps.data?.mimeType">{{ slotProps.data?.mimeType }}</span>
          <PSkeleton v-else width="5rem" height="1rem" />
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
          <VisibilityDisplayer
            v-if="slotProps.data" :key="slotProps.data.visibility"
            :visibility="slotProps.data.visibility"
          />
          <div v-else class="flex flex-row items-center gap-2">
            <PSkeleton size="1.5rem" shape="circle" />
            <PSkeleton width="5rem" height="1rem" />
          </div>
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
        <span v-if="slotProps.data" :key="slotProps.data.creationDate">{{
          d(slotProps.data.creationDate, { timeStyle: 'medium', dateStyle: 'short' })
        }}</span>
        <PSkeleton v-else width="8rem" height="1rem" />
      </template>
      <template #loading>
        <PSkeleton width="8rem" height="1rem" />
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
