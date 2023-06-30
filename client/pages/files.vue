<script lang="ts" setup>
import { useConfirm } from 'primevue/useconfirm'

const { t } = useI18n()
const toast = useToast()
const confirm = useConfirm()
const filters = useFilesFilters()

usePageName(() => t('pages.files.title'))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const openFiltersDialog = ref(false)
const openRenameDialog = ref(false)

const files = ref<Nullable<Array<FileRepresentationDTO>>>([])
const selectedRow = ref<Nullable<FileRepresentationDTO>>(null)
const selectedRows = ref<Array<FileRepresentationDTO>>([])
const selecting = computed(() => selectedRows.value.length > 0)
const selectedRowId = computed(() => selectedRow.value?.id)

/*
const _renameFile = useRenameFile((response) => {
  const file = files.value?.find((f: FileRepresentationDTO) => f.id === selectedRowId.value)
  if (file)
    Object.assign(file, response.data.payload)
})
function renameFile(name: string) {
  if (!selectedRowId.value)
    return

  _renameFile(selectedRowId.value, name)
    .finally(() => selectedRow.value = null)
}

const _deleteFile = useDeleteFile(() => {
  files.value = files.value?.filter((f: FileRepresentationDTO) => f.id !== selectedRowId.value)
})
function deleteFile() {
  if (!selectedRowId.value)
    return

  _deleteFile(selectedRowId.value)
    .finally(() => selectedRow.value = null)
}
function confirmDelete() {
  return confirm.require({
    message: t('pages.files.delete.warning'),
    header: t('pages.files.delete.title'),
    acceptIcon: 'i-tabler-trash',
    acceptLabel: t('pages.files.delete.submit'),
    rejectLabel: t('pages.files.delete.cancel'),
    acceptClass: 'p-button-danger',
    accept: deleteFile,
  })
}
const _changeFileVisibility = useChangeFileVisibility((response) => {
  const file = files.value?.find((f: FileRepresentationDTO) => f.id === selectedRowId.value)
  if (file)
    Object.assign(file, response.data.payload)
})
function updateFileVisibility(visibility: 'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE') {
  if (!selectedRowId.value)
    return

  _changeFileVisibility(selectedRowId.value, visibility)
    .finally(() => selectedRow.value = null)
}

 */

const { data, isFinished } = useAPI<PageableDTO>('/files/paged')
watch(isFinished, () => {
  if (isFinished.value)
    files.value = data.value?.page.items
})

/*
watch(copied, (val) => {
  if (val) {
    toast.add({
      severity: 'info',
      summary: t('pages.files.link_copied'),
      detail: {
        icon: 'i-tabler-clipboard-check',
      },
      life: 5000,
      closable: false,
    })
  }
})
 */
</script>

<template>
  <div class="h-full flex flex-col gap-4">
    <!-- Search bar and filters -->
    <div class="flex flex-row gap-4">
      <span class="flex-grow p-input-icon-left">
        <i class="i-tabler-search" />
        <PInputText class="w-full p-inputtext-lg" :placeholder="t('pages.files.search_by_name')" />
      </span>
      <PButton
        icon="i-tabler-filter"
        :label="t('pages.files.advanced_filters')"
        :outlined="filters.isEmpty.value"
        :badge="filters.isEmpty.value ? undefined : String(filters.activeFilters.value)"
        @click="openFiltersDialog = true"
      />
    </div>

    <!-- Main table -->
    <div class="p-card p-0 overflow-hidden flex-grow">
      <FilesTable
        v-if="files && files.length > 0"
        v-model:selectedRow="selectedRow"
        v-model:selectedRows="selectedRows"
        :files="files"
      />

      <div v-else class="h-full w-full flex flex-col justify-center items-center">
        <img class="w-10em" src="/assets/img/new_file.png" alt="New file">
        <h1 class="text-2xl">
          Looks pretty empty in here
        </h1>
        <p class="pb-10">
          You have not uploaded any file yet.
        </p>
        <PButton rounded label="Upload a file" />
      </div>
    </div>

    <!-- Action buttons -->
    <ActionButtons
      :selecting="selecting"
      @download="console.log('Download')"
      @change-visibility="console.log('Change visibility')"
      @unselect="console.log('Unselect')"
      @delete="console.log('Delete')"
    />

    <!-- Dialogs -->
    <FiltersDialog v-model:visible="openFiltersDialog" />
    <RenameDialog
      v-if="selectedRow"
      v-model:visible="openRenameDialog"
      :name="selectedRow.name"
    />
    <!--      @completed="renameFile" -->
    <PConfirmDialog :pt="{ rejectButton: { icon: { class: 'display-none' } } }" />
  </div>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
