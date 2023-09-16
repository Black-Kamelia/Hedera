<script lang="ts" setup>
import type { AutoCompleteCompleteEvent, AutoCompleteItemSelectEvent } from 'primevue/autocomplete'
import PAutoComplete from 'primevue/autocomplete'
import type { FileSize, FileSizeShift } from '~/composables/useHumanFileSize'

const { t } = useI18n()
const { filesSizeScale } = storeToRefs(useUserSettings())
const { computeShift } = useHumanFileSize()

const model = defineModel<Nullable<number>>()
const inputModel = ref<Nullable<FileSize>>(model.value ? computeShift(model.value) : null)

const sizes = computed<{ name: string; shift: FileSizeShift }[]>(() => {
  switch (filesSizeScale.value) {
    case 'BINARY': return [
      { name: t('size_units.binary.0'), shift: 0 },
      { name: t('size_units.binary.10'), shift: 10 },
      { name: t('size_units.binary.20'), shift: 20 },
      { name: t('size_units.binary.30'), shift: 30 },
      { name: t('size_units.binary.40'), shift: 40 },
    ]
    case 'DECIMAL': return [
      { name: t('size_units.decimal.0'), shift: 0 },
      { name: t('size_units.decimal.3'), shift: 3 },
      { name: t('size_units.decimal.6'), shift: 6 },
      { name: t('size_units.decimal.9'), shift: 9 },
      { name: t('size_units.decimal.12'), shift: 12 },
    ]
  }
})
const suggestions = ref<FileSize[]>([])

function searchSize(event: AutoCompleteCompleteEvent) {
  if (event.query.match(/^ *[0-9]+\.[0-9]+ *$/)) {
    suggestions.value = sizes.value
      .filter(item => item.shift !== 0)
      .map(item => ({
        value: Number(event.query).toFixed(2),
        shift: item.shift,
      }))
    return
  }
  if (event.query.match(/^ *[0-9]+ *$/)) {
    suggestions.value = sizes.value
      .map(item => ({
        value: Number(event.query).toFixed(2),
        shift: item.shift,
      }))
    return
  }
  suggestions.value = []
}

const el = ref<Nullable<CompElement<InstanceType<typeof PAutoComplete>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})

function labelFormat(item: FileSize) {
  switch (filesSizeScale.value) {
    case 'BINARY': return `${item.value} ${t(`size_units.binary.${item.shift}`)}`
    case 'DECIMAL': return `${item.value} ${t(`size_units.decimal.${item.shift}`)}`
  }
}
function onSelect(event: AutoCompleteItemSelectEvent) {
  inputModel.value = event.value
  switch (filesSizeScale.value) {
    case 'BINARY': return model.value = event.value ? Math.ceil(event.value.value * 2 ** event.value.shift) : null
    case 'DECIMAL': return model.value = event.value ? Math.ceil(event.value.value * 10 ** event.value.shift) : null
  }
}
watch(model, (val) => {
  inputModel.value = val ? computeShift(val) : null
})
</script>

<template>
  <PAutoComplete
    ref="el"
    v-model="inputModel"
    v-bind="$attrs"
    :suggestions="suggestions"
    :option-label="labelFormat"
    force-selection
    :pt="{ input: { class: 'w-full', style: { opacity: '100%' } } }"
    @complete="searchSize"
    @clear="model = null"
    @item-select="onSelect"
  >
    <template #empty>
      <p class="py-3 px-5 text-[--text-color-secondary]">
        {{ t('size_units.incorrect_format') }}
      </p>
    </template>
    <template #content>
      <p>coucou</p>
    </template>
  </PAutoComplete>
</template>
