<script setup lang="ts">
defineProps<{
  color?: string
}>()
</script>

<template>
  <div class="rounded bg-white text-black text-center items-center shadow p-4 w-3xl">
    <slot />
  </div>
</template>

<style scoped>
.rounded {
  border-radius: 1.5rem;
}
</style>
