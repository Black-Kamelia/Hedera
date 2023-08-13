<script lang="ts" setup>
import type { DataTablePageEvent, DataTableRowContextMenuEvent, DataTableSortMeta } from 'primevue/datatable'

const DEFAULT_PAGE = 0
const DEFAULT_PAGE_SIZE = 10
const DEFAULT_SORT: DataTableSortMeta[] = []

const { t, d } = useI18n()

const selectedRows = defineModel<Array<UserRepresentationDTO>>('selectedRows', { default: () => [] })
const selectedRow = ref<Nullable<UserRepresentationDTO>>(null)

const page = ref(DEFAULT_PAGE)
const pageSize = ref(DEFAULT_PAGE_SIZE)

const sort = ref<DataTableSortMeta[]>(DEFAULT_SORT)
const sortDefinition = computed(() => sort.value.map<SortObject>(({ field, order }) => ({
  field,
  direction: order === 1 ? 'ASC' : 'DESC',
})))

const pageDefinition = computed<PageDefinitionDTO>(() => ({
  sorter: sortDefinition.value,
}))

const { data, pending, error, refresh } = useLazyFetchAPI<PageableDTO<UserRepresentationDTO>>('/users/search', {
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

const createUserDialog = ref<boolean>(false)

function onPage(event: DataTablePageEvent) {
  page.value = event.page
  pageSize.value = event.rows
}

function onRowContextMenu(event: DataTableRowContextMenuEvent) {

}

function handleCreate() {
  refresh()
}
</script>

<template>
  <div class="p-card flex flex-row items-center gap-7 w-full overflow-hidden">
    <PDataTable
      v-model:selection="selectedRows"
      v-model:contextMenuSelection="selectedRow"
      v-model:multi-sort-meta="sort"
      class="w-full"
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
      @row-contextmenu="onRowContextMenu"
    >
      <PColumn sortable field="username" :header="t('pages.configuration.users.table.username')">
        <template #sorticon="slotProps">
          <SortIcon v-bind="slotProps" />
        </template>
        <template #body="slotProps">
          <span v-if="slotProps.data">{{ slotProps.data.username }}</span>
          <PSkeleton v-else width="5rem" height="1rem" />
        </template>
      </PColumn>

      <PColumn sortable field="email" :header="t('pages.configuration.users.table.email')">
        <template #sorticon="slotProps">
          <SortIcon v-bind="slotProps" />
        </template>
        <template #body="slotProps">
          <span v-if="slotProps.data">{{ slotProps.data.email }}</span>
          <PSkeleton v-else width="12.5rem" height="1rem" />
        </template>
      </PColumn>

      <PColumn sortable field="role" :header="t('pages.configuration.users.table.role')">
        <template #sorticon="slotProps">
          <SortIcon v-bind="slotProps" />
        </template>
        <template #body="slotProps">
          <Transition v-if="slotProps.data" name="fade" mode="out-in">
            <RoleDisplayer :key="slotProps.data.role" :role="slotProps.data.role" />
          </Transition>
          <div v-else class="flex flex-row items-center gap-2">
            <PSkeleton size="1.5rem" shape="circle" />
            <PSkeleton width="5rem" height="1rem" />
          </div>
        </template>
      </PColumn>

      <PColumn sortable field="enabled" :header="t('pages.configuration.users.table.status')">
        <template #sorticon="slotProps">
          <SortIcon v-bind="slotProps" />
        </template>
        <template #body="slotProps">
          <Transition v-if="slotProps.data" name="fade" mode="out-in">
            <StatusDisplayer :key="slotProps.data.enabled" :status="slotProps.data.enabled" />
          </Transition>
          <div v-else class="flex flex-row items-center gap-2">
            <PSkeleton size="1.5rem" shape="circle" />
            <PSkeleton width="5rem" height="1rem" />
          </div>
        </template>
      </PColumn>

      <PColumn sortable field="createdAt" :header="t('pages.configuration.users.table.creation_date')">
        <template #sorticon="slotProps">
          <SortIcon v-bind="slotProps" />
        </template>
        <template #body="slotProps">
          <span v-if="slotProps.data">{{ d(slotProps.data.createdAt) }}</span>
          <PSkeleton v-else width="8rem" height="1rem" />
        </template>
      </PColumn>
    </PDataTable>
  </div>

  <div class="flex flex-row-reverse">
    <PButton
      :label="t('pages.configuration.users.create_user')"
      icon="i-tabler-plus"
      outlined
      @click="createUserDialog = true"
    />
  </div>

  <CreateUserDialog v-model:visible="createUserDialog" @completed="handleCreate" />
</template>
