<script lang="ts" setup>
const { sorted, sortOrder = 0 } = defineProps<{ sorted: boolean; sortOrder?: number }>()
</script>

<template>
  <i
    class="ml-1 text-xs block" :class="{
      'i-tabler-arrows-sort': !sorted,
      'i-tabler-sort-descending': sortOrder > 0,
      'i-tabler-sort-ascending': sortOrder < 0,
    }"
  />
</template>
