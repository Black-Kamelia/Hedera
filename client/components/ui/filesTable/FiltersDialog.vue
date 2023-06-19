<script lang="ts" setup>
const { visible } = defineModels<{
  visible: boolean
}>()

const { t } = useI18n()

const value = ref(null)
const options = ref([
  { name: t('pages.files.visibility.public'), value: 1, icon: 'i-tabler-world' },
  { name: t('pages.files.visibility.unlisted'), value: 2, icon: 'i-tabler-link' },
  { name: t('pages.files.visibility.protected'), value: 4, disabled: true, icon: 'i-tabler-lock' },
  { name: t('pages.files.visibility.private'), value: 3, icon: 'i-tabler-eye-off' },
])

const selectedTypes = ref()
const types = ref([
  {
    name: 'Images',
    items: [
      { name: 'image/png' },
      { name: 'image/jpg' },
      { name: 'image/gif' },
    ],
  },
  {
    name: 'Vidéos',
    items: [
      { name: 'video/mp4' },
      { name: 'video/avi' },
      { name: 'video/mkv' },
    ],
  },
  {
    name: 'Documents',
    items: [
      { name: 'application/pdf' },
    ],
  },
  {
    name: 'Musiques',
    items: [
      { name: 'audio/mp3' },
      { name: 'audio/wav' },
      { name: 'audio/ogg' },
    ],
  },
  {
    name: 'Archives',
    items: [
      { name: 'application/zip' },
      { name: 'application/x-rar-compressed' },
    ],
  },
])

const startingDate = ref()
const endingDate = ref()
const minimumSize = ref()
const maximumSize = ref()
const minimumViews = ref()
const maximumViews = ref()

function reset() {
  value.value = null
  selectedTypes.value = null
  startingDate.value = null
  endingDate.value = null
  minimumSize.value = null
  maximumSize.value = null
  minimumViews.value = null
  maximumViews.value = null
}
</script>

<template>
  <PDialog v-model:visible="visible" modal header="Filtres avancés">
    <div class="grid grid-cols-1 xl:grid-cols-2 grid-gap-6 justify-items-stretch">
      <div class="flex flex-col gap-2">
        <h2>Visibilité</h2>
        <PSelectButton
          v-model="value" class="w-full" :options="options" option-label="name" multiple
          aria-labelledby="multiple" option-disabled="disabled"
        >
          <template #option="slotProps">
            <div class="flex flex-row gap-2">
              <i :class="slotProps.option.icon" />
              <span>{{ slotProps.option.name }}</span>
            </div>
          </template>
        </PSelectButton>
      </div>

      <div class="flex flex-col gap-2">
        <h2>Date de création</h2>
        <div class="flex flex-row gap-3">
          <PCalendar
            v-model="startingDate" class="w-full" placeholder="Date de début" show-button-bar show-time
            hour-format="24"
          />
          <PCalendar
            v-model="endingDate" class="w-full" placeholder="Date de fin" show-button-bar show-time
            hour-format="24"
          />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>Taille</h2>
        <div class="flex flex-row gap-3">
          <FormInputSize v-model="minimumSize" class="w-full" :pt="{ input: { class: 'w-full' } }" placeholder="Taille minimale" />
          <FormInputSize v-model="maximumSize" class="w-full" :pt="{ input: { class: 'w-full' } }" placeholder="Taille maximale" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>Vues</h2>
        <div class="flex flex-row gap-3">
          <PInputNumber v-model="minimumViews" class="w-full" placeholder="Nombre minimal de vues" />
          <PInputNumber v-model="maximumViews" class="w-full" placeholder="Nombre maximal de vues" />
        </div>
      </div>

      <div class="flex flex-col gap-2">
        <h2>Format</h2>
        <PMultiSelect
          v-model="selectedTypes" :options="types" option-label="name" option-group-label="name" option-group-children="items" placeholder="Tous les types"
          :max-selected-labels="3" class="w-full" filter selected-items-label="{0} types sélectionnés"
        />
      </div>

      <div class="flex flex-col gap-2">
        <h2>Propriétaire</h2>
        <PMultiSelect
          v-model="selectedTypes" :options="types" option-label="name" placeholder="Tout le monde"
          :max-selected-labels="3" class="w-full" selected-items-label="{0} types sélectionnés"
        />
      </div>
    </div>

    <template #footer>
      <PButton label="Réinitialiser" icon="i-tabler-arrow-back-up" text @click="reset" />
      <PButton label="Appliquer les filtres" icon="i-tabler-check" autofocus @click="visible = false" />
    </template>
  </PDialog>
</template>
