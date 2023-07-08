<script lang="ts" setup generic="T">
const { title, subtitle, radioName, value } = defineProps<{
  title: string
  subtitle?: string
  radioName: string
  value: T
}>()

const emit = defineEmits<{
  (event: 'change', newValue: T): void
}>()

const model = defineModel<T>()
const focus = ref(false)

function onClick() {
  model.value = value
  emit('change', value)
}
</script>

<template>
  <div
    class="h-radiocard"
    :class="{ 'checked': model === value, 'p-focus': focus }"
    tabindex="-1"
    @click="onClick"
    @focus="focus = true"
    @focusin="focus = true"
    @focusout="focus = false"
    @blur="focus = false"
  >
    <div class="flex flex-col items-stretch text-[var(--text-color)]">
      <div class="flex flex-row justify-between items-center gap-8">
        <div class="flex flex-col items-start">
          <span>{{ title }}</span>
          <small v-if="subtitle">{{ subtitle }}</small>
        </div>
        <PRadioButton v-model="model" :name="radioName" :value="value" />
      </div>
      <slot />
    </div>
  </div>
</template>
