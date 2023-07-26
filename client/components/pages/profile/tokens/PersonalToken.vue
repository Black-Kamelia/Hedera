<script lang="ts" setup>
const { token } = defineProps<{
  token: {
    id: string
    name: string
    creationDate: string
    lastUsed: string
  }
}>()
</script>

<template>
  <div class="flex flex-row items-center gap-4 w-full">
    <i class="i-tabler-key text-3xl text-green" />
    <div class="flex flex-row justify-between items-center flex-grow">
      <div class="flex flex-col">
        <h2 class="text-xl mb-1">
          {{ token.name }}
        </h2>
        <span class="text-[--text-color-secondary]">
          Created on {{ token.creationDate }}
        </span>
        <span class="text-[--text-color-secondary]">
          Last used on {{ token.lastUsed }}
        </span>
      </div>
      <PButton icon="i-tabler-trash" severity="danger" text rounded />
    </div>
  </div>
</template>
