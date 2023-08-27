<script lang="ts" setup>
import type { AutoCompleteCompleteEvent } from 'primevue/autocomplete'
import PAutoComplete from 'primevue/autocomplete'

const model = defineModel<Nullable<FileSize>>()
const { t } = useI18n()

const sizes: { name: string; shift: 0 | 10 | 20 | 30 | 40 | 50 }[] = [
  { name: t('size_units.binary.0'), shift: 0 },
  { name: t('size_units.binary.10'), shift: 10 },
  { name: t('size_units.binary.20'), shift: 20 },
  { name: t('size_units.binary.30'), shift: 30 },
  { name: t('size_units.binary.40'), shift: 40 },
  { name: t('size_units.binary.50'), shift: 50 },
]
const suggestions = ref<FileSize[]>([])

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
    v-model="model"
    v-bind="$attrs"
    :suggestions="suggestions"
    :option-label="(item: FileSize) => `${item.value} ${t(`size_units.binary.${item.shift}`)}`"
    force-selection
    :pt="{ input: { class: 'w-full', style: { opacity: '100%' } } }"
    @complete="searchSize"
    @item-select="model = $event.value"
  >
    <template #empty>
      <p class="py-3 px-5 text-[--text-color-secondary]">
        {{ t('size_units.incorrect_format') }}
      </p>
    </template>
  </PAutoComplete>
</template>
