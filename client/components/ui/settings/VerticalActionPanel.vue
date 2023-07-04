<script lang="ts" setup>
const { header, description } = defineProps<{
  header: string
  description: string
}>()
</script>

<template>
  <div class="p-card p-7">
    <div class="flex flex-row items-center flex-wrap">
      <h2 class="text-lg font-bold mr-3">
        {{ header }}
      </h2>
      <slot name="header-ornement" />
    </div>
    <div class="flex flex-col gap-4 mt-2">
      <p class="p-text-secondary">
        {{ description }}
      </p>
      <div>
        <slot />
      </div>
    </div>
  </div>
</template>
