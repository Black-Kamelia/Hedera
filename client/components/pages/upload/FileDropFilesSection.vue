<script setup lang="ts">
defineProps<{
  files: File[]
  completed: boolean
  removeCallback: (index: number) => void
}>()

const { t } = useI18n()
const { format } = useHumanFileSize()

function getImageData(file: File) {
  return (file as any).objectURL
}
</script>

<template>
  <div
    v-for="file in files"
    :key="file.name + file.type + file.size"
    class="m-0 px-6 flex flex-col items-center w-full gap-3 border-1 border-gray:50 rounded-lg"
  >
    <div v-if="getImageData(file)">
      <img role="presentation" :alt="file.name" :src="getImageData(file)" width="100" height="50" class="shadow-2">
    </div>
    <span class="font-semibold">{{ file.name }}</span>
    <div>{{ format({ value: file.size, shift: 10 }) }}</div>
    <PBadge v-if="completed" :value="t('pages.upload.completed')" class="mt-3" severity="success" />
    <PBadge v-else :value="t('pages.upload.pending')" class="mt-3" severity="warning" />
    <PButton icon="i-tabler-x" text rounded severity="danger" @click="removeCallback" />
  </div>
</template>
