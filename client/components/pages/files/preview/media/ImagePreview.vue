<script setup lang="ts">
const { file } = defineProps<{
  file: FileRepresentationDTO
}>()

const previewOpen = defineModel<boolean>('open', { default: false })

const { t } = useI18n()
const { data, pending, error, execute, status } = useAsyncData(`${file.id}_preview`, () => {
  return $fetchAPI<Blob>(`/files/${file.code}`)
    .then(blob => URL.createObjectURL(blob))
}, { immediate: false })

watch(previewOpen, (val) => {
  if (val && status.value !== 'success' && !error.value) execute()
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

const controls = [
  {
    icon: 'i-tabler-rotate-clockwise-2',
    tooltipKey: 'pages.files.preview.controls.rotate_right',
    command: rotateRight,
  },
  {
    icon: 'i-tabler-rotate-2',
    tooltipKey: 'pages.files.preview.controls.rotate_left',
    command: rotateLeft,
  },
  {
    icon: 'i-tabler-zoom-in',
    tooltipKey: 'pages.files.preview.controls.zoom_in',
    command: zoomIn,
    disabled: computed(() => zoom.value >= 2),
  },
  {
    icon: 'i-tabler-zoom-out',
    tooltipKey: 'pages.files.preview.controls.zoom_out',
    command: zoomOut,
    disabled: computed(() => zoom.value <= 0.5),
  },
]
</script>

<template>
  <MediaPreview
    v-model:open="previewOpen" :file="file" :controls="controls" @close="reset()"
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
      class="pointer-events-auto m-auto max-h-full rounded-lg object-contain image-preview select-none"
      :style="{
        transform: `rotate(${rotation}deg) scale(${zoom})`,
      }"
      :src="data!"
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
