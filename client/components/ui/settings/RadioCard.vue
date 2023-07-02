<script lang="ts" setup>
const { title, subtitle, radioName, value } = defineProps<{
  title: string
  subtitle?: string
  radioName: string
  value: string
}>()

const model = defineModel<any>()
</script>

<template>
  <div class="p-5 rounded-lg border-1 border-[var(--surface-border)] hover:border-[var(--primary-500)] bg-[--surface-c] cursor-pointer" @click="model = value">
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
