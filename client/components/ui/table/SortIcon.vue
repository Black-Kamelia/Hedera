<script lang="ts" setup>
const { sorted, sortOrder } = defineProps<{ sorted: boolean; sortOrder: boolean }>()
const order = computed(() => Number(sortOrder))
</script>

<template>
  <i
    class="ml-1 text-xs block" :class="{
      'i-tabler-arrows-sort': !sorted,
      'i-tabler-sort-descending': order > 0,
      'i-tabler-sort-ascending': order < 0,
    }"
  />
</template>
