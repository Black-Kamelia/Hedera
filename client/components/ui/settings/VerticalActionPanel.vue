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
    <div class="flex flex-col gap-4">
      <p class="p-text-secondary">
        {{ description }}
      </p>
      <div>
        <slot />
      </div>
    </div>
  </div>
</template>
