<script setup lang="ts">
defineProps<{
  files: File[]
  completed: boolean
}>()

defineEmits<{
  (event: 'remove', index: number): void
}>()

const { t } = useI18n()
const { autoFormat } = useHumanFileSize()

function getImageData(file: File) {
  return (file as any).objectURL
}

function getIcon(type: string) {
  if (type.startsWith('audio/')) return 'i-tabler-music'
  if (type.startsWith('video/')) return 'i-tabler-video'
  if (type.startsWith('text/')) return 'i-tabler-file-text'
  if (type === 'application/zip') return 'i-tabler-file-zip'
  if (type === 'application/pdf') return 'i-tabler-file-text'
  if (type === 'application/unknown') return 'i-tabler-file-unknown'
  return 'i-tabler-file'
}
</script>

<template>
  <div
    v-for="(file, index) of files"
    :key="file.name + file.type + file.size"
    class="flex flex-wrap items-center mb-4 gap-3 p-2 border border-[var(--primary-color-transparent)] rounded-xl"
  >
    <div class="relative w-6rem h-4rem border-rounded-2 overflow-hidden">
      <div
        class="absolute flex flex-center w-full h-full bg-[var(--primary-color-transparent)] backdrop-blur-sm border-rounded-2 w-full h-full text-white"
      >
        <img v-if="getImageData(file)" role="presentation" :alt="file.name" :src="getImageData(file)" width="50" class="w-6rem h-4rem object-cover">
        <i v-else :class="getIcon(file.type)" />
      </div>
    </div>

    <div>
      <div>{{ file.name }}</div>
      <span class="mr-2">{{ autoFormat(file.size) }}</span>
      <PBadge :value="t(`pages.upload.${completed ? 'completed' : 'pending'}`)" :severity="completed ? 'success' : 'primary'" />
    </div>
    <div class="ml-auto">
      <PButton text rounded severity="danger" @click="$emit('remove', index)">
        <i class="i-tabler-x" />
      </PButton>
    </div>
  </div>
</template>
