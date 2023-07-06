<script lang="ts" setup>
const { title, subtitle, radioName, value } = defineProps<{
  title: string
  subtitle?: string
  radioName: string
  value: string
}>()

const emit = defineEmits<{
  (event: 'change', newValue: typeof value): void
}>()

const model = defineModel<any>()
const focus = ref(false)
</script>

<template>
  <div
    class="h-radiocard"
    :class="{ 'checked': model === value, 'p-focus': focus }"
    tabindex="-1"
    @click="() => {
      model = value
      emit('change', value)
    }"
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
