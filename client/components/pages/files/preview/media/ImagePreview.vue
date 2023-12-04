<script setup lang="ts">
const { file } = defineProps<{
  file: FileRepresentationDTO
}>()

const previewOpen = defineModel<boolean>('open', { default: false })

const rotation = ref(0)
const zoom = ref(1)

function reset() {
  rotation.value = 0
  zoom.value = 1
}

function rotateLeft() {
  rotation.value = rotation.value - 90 % 360
}

function rotateRight() {
  rotation.value = rotation.value + 90 % 360
}

function zoomIn() {
  if (zoom.value < 2) zoom.value = zoom.value + 0.25
}

function zoomOut() {
  if (zoom.value > 0.5) zoom.value = zoom.value - 0.25
}
</script>

<template>
  <MediaPreview
    v-model:open="previewOpen" :file="file" :controls="[
      {
        icon: 'i-tabler-rotate-clockwise-2',
        command() { rotateLeft() },
      },
      {
        icon: 'i-tabler-rotate-2',
        command() { rotateRight() },
      },
      {
        icon: 'i-tabler-zoom-in',
        command() { zoomIn() },
      },
      {
        icon: 'i-tabler-zoom-out',
        command() { zoomOut() },
      },
    ]" @close="reset()"
  >
    <img
      class="pointer-events-auto z-1000 max-h-full rounded-lg object-contain image-preview select-none"
      :style="{
        transform: `rotate(${rotation}deg) scale(${zoom})`,
      }"
      :src="`http://localhost:8080/m/${file.code}`"
      :alt="file.name"
    >

    <template #controls>
      <PButton
        icon="i-tabler-rotate-clockwise-2"
        size="small"
        text
        rounded
        @click="rotateRight()"
      />
      <PButton
        icon="i-tabler-rotate-2"
        size="small"
        text
        rounded
        @click="rotateLeft()"
      />
      <PButton
        icon="i-tabler-zoom-in"
        size="small"
        text
        rounded
        :disabled="zoom >= 2"
        @click="zoomIn()"
      />
      <PButton
        icon="i-tabler-zoom-out"
        size="small"
        text
        rounded
        :disabled="zoom <= 0.5"
        @click="zoomOut()"
      />
    </template>
  </MediaPreview>
</template>

<style scoped>
.image-preview {
  transition: transform 0.2s ease;
}
</style>
