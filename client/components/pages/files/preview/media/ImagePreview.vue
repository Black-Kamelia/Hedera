<script setup lang="ts">
const { file } = defineProps<{
  file: FileRepresentationDTO
}>()

const previewOpen = defineModel<boolean>('open', { default: false })

const { t } = useI18n()
const { data, pending, error, execute } = useLazyFetchAPI<Blob>(`/files/${file.code}`, { immediate: false })
const base64Data = computed(() => data.value ? URL.createObjectURL(data.value) : null)

watch(previewOpen, (val) => {
  if (val) execute()
})

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
        command() { rotateRight() },
      },
      {
        icon: 'i-tabler-rotate-2',
        command() { rotateLeft() },
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
    <PProgressSpinner
      v-if="pending" :pt="{
        circle: {
          style: {
            stroke: 'white',
            animation: 'p-progress-spinner-dash 1.5s ease-in-out infinite',
          },
        },
      }"
    />
    <img
      v-else-if="!pending && !error"
      class="pointer-events-auto z-1000 max-h-full rounded-lg object-contain image-preview select-none"
      :style="{
        transform: `rotate(${rotation}deg) scale(${zoom})`,
      }"
      :src="base64Data"
      :alt="file.name"
    >
    <div v-else class="text-white flex flex-col items-center justify-center gap-5">
      <i class="i-tabler-photo-exclamation text-5xl" />
      <h1 class="text-2xl font-semibold">
        {{ t('pages.files.preview.failed') }}
      </h1>
    </div>
  </MediaPreview>
</template>

<style scoped>
.image-preview {
  transition: transform 0.2s ease;
}
</style>
