<script lang="ts" setup>
const { visible } = defineModels<{
  visible: boolean
}>()

const value = ref(null)
const options = ref([
  { name: 'Public', value: 1, icon: 'i-tabler-world' },
  { name: 'Non répertorié', value: 2, icon: 'i-tabler-link' },
  { name: 'Protégé', value: 4, disabled: true, icon: 'i-tabler-lock' },
  { name: 'Privé', value: 3, icon: 'i-tabler-eye-off' },
])

const selectedCities = ref()
const cities = ref([
  { name: 'Images', code: 'NY' },
  { name: 'Vidéos', code: 'RM' },
  { name: 'Documents', code: 'LDN' },
  { name: 'Musiques', code: 'IST' },
  { name: 'Archives', code: 'PRS' },
])

const test = ref(0)
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
        <h2>Date de mise en ligne</h2>
        <div class="flex flex-row gap-3">
          <PCalendar
            v-model="value" class="w-full" placeholder="Date de début" show-button-bar show-time
            hour-format="24"
          />
          <PCalendar
            v-model="value" class="w-full" placeholder="Date de fin" show-button-bar show-time
            hour-format="24"
          />
        </div>
      </div>
      <div class="flex flex-col gap-2">
        <h2>Taille</h2>
        <div class="flex flex-row gap-3">
          <FormInputSize v-model="test" class="w-full" :pt="{ input: { class: 'w-full' } }" placeholder="Taille minimale" />
          {{ test }}
          <FormInputSize class="w-full" :pt="{ input: { class: 'w-full' } }" placeholder="Taille maximale" />
        </div>
      </div>
      <div class="flex flex-col gap-2">
        <h2>Vues</h2>
        <div class="flex flex-row gap-3">
          <PInputNumber v-model="value" class="w-full" placeholder="Nombre minimal de vues" />
          <PInputNumber v-model="value" class="w-full" placeholder="Nombre maximal de vues" />
        </div>
      </div>
      <div class="flex flex-col gap-2 xl:grid-col-start-1 xl:grid-col-end-3">
        <h2>Types de fichiers</h2>
        <PMultiSelect
          v-model="selectedCities" :options="cities" option-label="name" placeholder="Tous les types"
          :max-selected-labels="3" class="w-full" selected-items-label="{0} types sélectionnés"
        />
      </div>
    </div>

    <template #footer>
      <PButton label="Réinitialiser" icon="i-tabler-arrow-back-up" text />
      <PButton label="Appliquer les filtres" icon="i-tabler-check" autofocus @click="visible = false" />
    </template>
  </PDialog>
</template>
