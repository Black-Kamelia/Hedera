<script lang="ts" setup>
import type { DataTableRowClickEvent } from 'primevue/datatable'
import type { MenuItem } from 'primevue/menuitem'
import { PContextMenu } from '#components'

const { locale, t } = useI18n()

usePageName(t('pages.files.title'))
definePageMeta({
  layout: 'sidebar',
  middleware: ['auth'],
})

const products = ref([
  {
    id: 1,
    code: 'A121',
    name: 'chrome_E5Hg6FCBkL.png',
    views: 123,
    size: 2345432,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 2,
    code: 'A121',
    name: 'chrome_LvUWJOAjPk.mp3',
    views: 123,
    size: 3456654,
    type: 'audio/mpeg',
    owner: 'Slama',
    visibility: 'UNLISTED',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 3,
    code: 'A121',
    name: 'chrome_NdoSonM48z.png',
    views: 123,
    size: 8765456,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PROTECTED',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 4,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 12345654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PRIVATE',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 5,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 45654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PRIVATE',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUeBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'imagee/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
  {
    id: 6,
    code: 'A121',
    name: 'chrome_uaf5sdrhrj.png',
    views: 123,
    size: 453654,
    type: 'image/png',
    owner: 'Slama',
    visibility: 'PUBLIC',
    uploaded_at: '2021-09-01 12:00:00',
    quantity: 10,
  },
])
const selectedProduct = ref()
const selecting = computed(() => selectedProduct.value?.length > 0)

const cm = ref<Nullable<CompElement<InstanceType<typeof PContextMenu>>>>()
const menuModel = ref<MenuItem[]>([
  {
    label: 'Ouvrir',
    icon: 'i-tabler-external-link',
  },
  {
    label: 'Renommer',
    icon: 'i-tabler-pencil',
  },
  {
    label: 'Changer la visibilité',
    icon: 'i-tabler-eye',
    items: [
      {
        label: 'Public',
        icon: 'i-tabler-world',
      },
      {
        label: 'Non répertorié',
        icon: 'i-tabler-link',
      },
      {
        label: 'Privé',
        icon: 'i-tabler-eye-off',
      },
    ],
  },
  {
    label: 'Copier le lien',
    icon: 'i-tabler-link',
  },
  {
    label: 'Télécharger',
    icon: 'i-tabler-download',
  },
  { separator: true },
  {
    label: 'Supprimer',
    icon: 'i-tabler-trash',
  },
])

function onRowContextMenu(event: DataTableRowClickEvent) {
  cm.value?.show(event.originalEvent)
}
</script>

<template>
  <div class="h-full flex flex-col gap-4">
    <div class="flex flex-row gap-4">
      <span class="flex-grow p-input-icon-left">
        <i class="i-tabler-search" />
        <PInputText class="w-full p-inputtext-lg" placeholder="Rechercher..." />
      </span>
      <PButton icon="i-tabler-filter" label="Filtres avancés" />
    </div>
    <div class="p-card p-0 overflow-hidden flex-grow">
      <PContextMenu ref="cm" :model="menuModel" />
      <PDataTable
        v-model:selection="selectedProduct"
        :value="products"
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
        <PColumn field="code" header="Aperçu" :sortable="false">
          <template #body="slotProps">
            <MediaPreview :data="slotProps.data" />
          </template>
          <template #loading>
            <PSkeleton width="6rem" height="4rem" />
          </template>
        </PColumn>
        <PColumn field="name" sortable header="Nom">
          <template #body="slotProps">
            <div class="flex flex-col gap-1">
              <span>{{ slotProps.data.name }}</span>
              <div class="flex flex-row items-center gap-1">
                <i class="i-tabler-eye text-xs" />
                <span class="text-xs">{{ slotProps.data.views }}</span>
              </div>
            </div>
          </template>
          <template #loading>
            <div class="flex flex-col gap-1">
              <PSkeleton width="10rem" height="1rem" />
              <PSkeleton width="2rem" height=".75rem" />
            </div>
          </template>
        </PColumn>
        <PColumn field="size" sortable header="Taille">
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
            {{ humanSize(slotProps.data.size, locale, t) }}
          </template>
          <template #loading>
            <PSkeleton width="5rem" height="1rem" />
          </template>
        </PColumn>
        <PColumn field="type" sortable header="Format">
          <template #loading>
            <PSkeleton width="5rem" height="1rem" />
          </template>
        </PColumn>
        <PColumn field="owner" sortable header="Propriétaire">
          <template #loading>
            <PSkeleton width="5rem" height="1rem" />
          </template>
        </PColumn>
        <PColumn field="visibility" sortable header="Visibilité">
          <template #body="slotProps">
            <div v-if="slotProps.data.visibility === 'PUBLIC'" class="flex flex-row items-center gap-2">
              <i class="i-tabler-world" />
              <span>{{ t('pages.files.visibility.public') }}</span>
            </div>
            <div v-else-if="slotProps.data.visibility === 'UNLISTED'" class="flex flex-row items-center gap-2">
              <i class="i-tabler-link" />
              <span>{{ t('pages.files.visibility.unlisted') }}</span>
            </div>
            <div v-else-if="slotProps.data.visibility === 'PROTECTED'" class="flex flex-row items-center gap-2">
              <i class="i-tabler-lock" />
              <span>{{ t('pages.files.visibility.protected') }}</span>
            </div>
            <div v-else-if="slotProps.data.visibility === 'PRIVATE'" class="flex flex-row items-center gap-2">
              <i class="i-tabler-eye-off" />
              <span>{{ t('pages.files.visibility.private') }}</span>
            </div>
            <div v-else class="flex flex-row items-center gap-2">
              <i class="i-tabler-help-triangle-filled" />
              <span>{{ t('pages.files.visibility.unknown') }}</span>
            </div>
          </template>
          <template #loading>
            <div class="flex flex-row items-center gap-2">
              <PSkeleton size="1.5rem" shape="circle" />
              <PSkeleton width="5rem" height="1rem" />
            </div>
          </template>
        </PColumn>
        <PColumn field="uploaded_at" sortable header="Mise en ligne">
          <template #loading>
            <PSkeleton width="8rem" height="1rem" />
          </template>
        </PColumn>
      </PDataTable>
    </div>

    <Transition>
      <div v-show="selecting" class="fixed bottom-8 left-50% translate-x--50% flex flex-row gap-2">
        <PButton
          class="delete-toast shadow-lg" label="Supprimer les fichiers sélectionnés" icon="i-tabler-trash"
          severity="danger" rounded
        />
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.v-enter-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.v-leave-active {
  transition: all 0.35s cubic-bezier(0.36, 0, 0.66, -0.56);
}

.v-enter-from,
.v-leave-to {
  transform: translateX(-50%) translateY(150%);
  opacity: 0;
}

tr.p-highlight .icon-preview {
}

html.dark .icon-preview {
}

html.dark tr.p-highlight .icon-preview {
}
</style>
