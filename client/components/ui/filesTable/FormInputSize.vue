<script lang="ts" setup>
import type { AutoCompleteCompleteEvent, AutoCompleteProps } from 'primevue/autocomplete'
import PAutoComplete from 'primevue/autocomplete'

export interface FormInputSizeProps extends OnlyProps<AutoCompleteProps> {}

interface Size {
  value: number
  shift: number
}

defineProps<FormInputSizeProps>()
const emit = defineEmits<{
  (event: 'update:modelValue', value: Size): void
}>()
const { modelValue } = defineModels<{
  modelValue: Size
}>()
const { t } = useI18n()

const value = ref<Nullable<Size>>(modelValue.value)
const sizes = [
  { name: t('sizeUnits.binary.0'), shift: 0 },
  { name: t('sizeUnits.binary.10'), shift: 10 },
  { name: t('sizeUnits.binary.20'), shift: 20 },
  { name: t('sizeUnits.binary.30'), shift: 30 },
  { name: t('sizeUnits.binary.40'), shift: 40 },
  { name: t('sizeUnits.binary.50'), shift: 50 },
  { name: t('sizeUnits.binary.60'), shift: 60 },
  { name: t('sizeUnits.binary.70'), shift: 70 },
  { name: t('sizeUnits.binary.80'), shift: 80 },
]
const suggestions = ref<Size[]>([])

function searchSize(event: AutoCompleteCompleteEvent) {
  if (event.query.match(/^ *[0-9]+\.[0-9]+ *$/)) {
    suggestions.value = sizes
      .filter(item => item.shift !== 0)
      .map(item => ({
        value: Number(event.query),
        shift: item.shift,
      }))
    return
  }
  if (event.query.match(/^ *[0-9]+ *$/)) {
    suggestions.value = sizes
      .map(item => ({
        value: Number(event.query),
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
</script>

<template>
  <PAutoComplete
    ref="el"
    v-model="value"
    :suggestions="suggestions"
    :option-label="(item: Size) => `${item.value} ${t(`sizeUnits.binary.${item.shift}`)}`"
    force-selection
    @complete="searchSize"
    @item-select="emit('update:modelValue', $event.value)"
    @change="!value ? emit('update:modelValue', { value: 0, shift: 0 }) : null"
  />
</template>
