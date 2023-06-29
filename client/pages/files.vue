<script lang="ts" setup>
import type { DataTableRowClickEvent } from 'primevue/datatable'

import { useConfirm } from 'primevue/useconfirm'
import { PContextMenu } from '#components'
import ActionButtons from '~/components/ui/filesTable/ActionButtons.vue'

const { locale, t, d, m } = useI18n()
const toast = useToast()
const confirm = useConfirm()
const axios = useAxiosFactory()

usePageName(() => t('pages.files.title'))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const files = ref<Nullable<Array<FileRepresentationDTO>>>([])
const selectedRow = ref<Nullable<FileRepresentationDTO>>(null)
const selectedRows = ref<Array<FileRepresentationDTO>>([])

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

function deleteFile() {
  if (!selectedRow.value)
    return

  const id = selectedRow.value!.id
  axios().delete(`/files/${id}`)
    .then((response) => {
      files.value = files.value?.filter((f: FileRepresentationDTO) => f.id !== id)
      return response
    })
    .then(response => toast.add({
      severity: 'success',
      summary: m(response.data.title),
      detail: { text: m(response.data.message) },
      life: 5000,
    }))
    .catch(error => toast.add({
      severity: 'error',
      summary: t('pages.files.delete.error'),
      detail: { text: m(error) },
      life: 5000,
    }))
    .finally(() => selectedRow.value = null)
  // files.value = files.value?.filter((f: FileRepresentationDTO) => f.id !== id)
}

function renameFile(name: string) {
  // TODO: handle error
  if (!selectedRow.value)
    return

  if (name === selectedRow.value!.name)
    return

  const id = selectedRow.value!.id
  axios().put(`/files/${id}/name`, { name })
    .then((response) => {
      const file = files.value?.find((f: FileRepresentationDTO) => f.id === id)
      if (file)
        Object.assign(file, response.data.payload)
      return response
    })
    .then(response => toast.add({
      severity: 'success',
      summary: m(response.data.title),
      detail: { text: m(response.data.message) },
      life: 5000,
    }))
    .catch(error => toast.add({
      severity: 'error',
      summary: t('pages.files.rename.error'),
      detail: { text: m(error) },
      life: 5000,
    }))
    .finally(() => selectedRow.value = null)
}

function updateFileVisibility(visibility: 'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE') {
  // TODO: handle error
  if (!selectedRow.value)
    return

  const id = selectedRow.value!.id
  axios().put(`/files/${id}/visibility`, { visibility })
    .then((response) => {
      const file = files.value?.find((f: FileRepresentationDTO) => f.id === id)
      if (file)
        Object.assign(file, response.data.payload)
      return response
    })
    .then(response => toast.add({
      severity: 'success',
      summary: m(response.data.title),
      detail: { text: m(response.data.message) },
      life: 5000,
    }))
    .catch(error => toast.add({
      severity: 'error',
      summary: t('pages.files.changeVisibility.error'),
      detail: { text: m(error) },
      life: 5000,
    }))
    .finally(() => selectedRow.value = null)
}

const { data, isFinished } = useAPI<PageableDTO>('/files/paged')
watch(isFinished, () => {
  if (isFinished.value)
    files.value = data.value?.page.items
})

const selecting = computed(() => selectedRows.value.length > 0)
const openFiltersDialog = ref(false)
const openRenameDialog = ref(false)

const cm = ref<Nullable<CompElement<InstanceType<typeof PContextMenu>>>>(null)

const { copy, copied, isSupported } = useClipboard()
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

const menuModel = computed(() => [
  {
    label: t('pages.files.contextMenu.open'),
    icon: 'i-tabler-external-link',
    command() {
      if (!selectedRow.value)
        return
      window.open(`/${selectedRow.value!.code}`)
    },
  },
  {
    label: t('pages.files.contextMenu.rename'),
    icon: 'i-tabler-pencil',
    command() {
      if (!selectedRow.value)
        return
      openRenameDialog.value = true
    },
  },
  {
    label: t('pages.files.contextMenu.changeVisibility'),
    icon: 'i-tabler-eye',
    items: [
      {
        label: t('pages.files.visibility.public'),
        icon: 'i-tabler-world',
        command() {
          updateFileVisibility('PUBLIC')
        },
      },
      {
        label: t('pages.files.visibility.unlisted'),
        icon: 'i-tabler-link',
        command() {
          updateFileVisibility('UNLISTED')
        },
      },
      {
        label: t('pages.files.visibility.protected'),
        icon: 'i-tabler-lock',
        disabled: true,
        command() {
          updateFileVisibility('PROTECTED')
        },
      },
      {
        label: t('pages.files.visibility.private'),
        icon: 'i-tabler-eye-off',
        command() {
          updateFileVisibility('PRIVATE')
        },
      },
    ],
  },
  {
    label: t('pages.files.contextMenu.copyLink'),
    icon: 'i-tabler-link',
    disabled: !isSupported.value,
    command() {
      if (!selectedRow.value)
        return
      copy(`${location.origin}/${selectedRow.value!.code}`)
    },
  },
  {
    label: t('pages.files.contextMenu.download'),
    icon: 'i-tabler-download',
    command() {
      if (!selectedRow.value)
        return
      useAPI(`/files/${selectedRow.value!.code}`, { responseType: 'blob' })
        .then(response => downloadBlob(response.data.value, selectedRow.value!.name))
    },
  },
  { separator: true },
  {
    label: t('pages.files.contextMenu.delete'),
    icon: 'i-tabler-trash',
    command() {
      confirmDelete()
    },
  },
])

function onRowContextMenu(event: DataTableRowClickEvent) {
  cm.value?.show(event.originalEvent)
}

const filters = useFilesFilters()
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
        icon="i-tabler-filter" :label="t('pages.files.advanced_filters')" :outlined="filters.isEmpty.value"
        :badge="filters.isEmpty.value ? undefined : String(filters.activeFilters.value)"
        @click="openFiltersDialog = true"
      />
    </div>

    <!-- Main table -->
    <div class="p-card p-0 overflow-hidden flex-grow">
      <PContextMenu ref="cm" :model="menuModel" />
      <PDataTable
        v-if="files && files.length > 0"
        v-model:selection="selectedRows"
        v-model:contextMenuSelection="selectedRow"
        :value="files"
        data-key="id"
        scrollable
        scroll-height="100%"
        sort-mode="multiple"
        removable-sort
        class="h-full"
        context-menu
        @row-contextmenu="onRowContextMenu"
      >
        <PColumn selection-mode="multiple" />

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

        <!--
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
        -->

        <PColumn field="owner.username" sortable :header="t('pages.files.table.owner')">
          <template #sorticon="slotProps">
            <i
              class="pointer-events-none ml-1 text-xs block" :class="{
                'i-tabler-arrows-sort': !slotProps.sorted,
                'i-tabler-sort-descending': Number(slotProps.sortOrder) > 0,
                'i-tabler-sort-ascending': Number(slotProps.sortOrder) < 0,
              }"
            />
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
            {{ d(slotProps.data.creationDate, { timeStyle: 'medium', dateStyle: 'short' }) }}
          </template>
          <template #loading>
            <PSkeleton width="8rem" height="1rem" />
          </template>
        </PColumn>
      </PDataTable>

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
      @completed="renameFile"
    />
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
