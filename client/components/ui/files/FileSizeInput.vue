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
  const match = event.query.match(/^ *([0-9]+(?:\.[0-9]*)?) *(\w*?) *$/)
  if (match === null) {
    suggestions.value = []
    return
  }

  const value = Number(match[1])
  const decimalValue = match[1].match(/[0-9]+\.[0-9]+/)
  const unit = match[2].toLowerCase()
  const shift = sizes.value.find(item => item.name.toLowerCase() === unit)?.shift

  if (shift !== undefined) {
    if (shift === 0 && decimalValue) {
      suggestions.value = []
      return
    }
    suggestions.value = [{ value: value.toFixed(2).toString(), shift }]
  } else {
    let results = sizes.value
      .filter(item => item.name.toLowerCase().startsWith(unit))
      .map(item => ({ value: value.toString(), shift: item.shift }))

    if (decimalValue) {
      results = results.filter(item => item.shift !== 0)
    }

    suggestions.value = results
  }
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
function select(item: FileSize) {
  inputModel.value = item
  switch (filesSizeScale.value) {
    case 'BINARY': return model.value = item ? Math.ceil(Number(item.value) * 2 ** item.shift) : null
    case 'DECIMAL': return model.value = item ? Math.ceil(Number(item.value) * 10 ** item.shift) : null
  }
}

function onSelect(event: AutoCompleteItemSelectEvent) {
  suggestions.value = []
  select(event.value)
}
function onBlur() {
  if (suggestions.value.length === 1) {
    select(suggestions.value[0])
    return
  }
  if (model.value === null) {
    inputModel.value = null
    model.value = null
    return
  }
  if (model.value !== undefined) {
    inputModel.value = computeShift(model.value)
  }
}
watch(model, (val) => {
  inputModel.value = val !== null && val !== undefined ? computeShift(val) : null
}, { immediate: true })
</script>

<template>
  <PAutoComplete
    ref="el"
    v-model="inputModel"
    v-bind="$attrs"
    :suggestions="suggestions"
    :option-label="labelFormat"

    :pt="{ input: { class: 'w-full', style: { opacity: '100%' } } }"
    @complete="searchSize"
    @clear="model = null"
    @item-select="onSelect"
    @blur="onBlur"
  >
    <template #empty>
      <p class="py-3 px-5 text-[--text-color-secondary]">
        {{ t('size_units.incorrect_format') }}
      </p>
    </template>
  </PAutoComplete>
</template>
