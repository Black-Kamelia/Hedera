<script lang="ts" setup>
const { header, description } = defineProps<{
  header: string
  description: string
}>()
</script>

<template>
  <div class="p-card p-7">
    <h2 class="text-lg font-bold mb-2">
      {{ header }}
    </h2>
    <div class="flex items-start flex-col gap-4 md:flex-row md:gap-12">
      <div class="flex items-start flex-grow flex-col gap-2">
        <p class="flex-grow p-text-secondary">
          {{ description }}
        </p>
        <slot name="cta" />
      </div>
      <div>
        <slot />
      </div>
    </div>
  </div>
</template>
