<script setup lang="ts">
const { file } = defineProps<{
  file: FileRepresentationDTO
}>()

const previewOpen = defineModel<boolean>('open', { default: false })

const { t } = useI18n()
const { data, pending, error, execute, status } = useAsyncData(`${file.id}_preview`, async () => {
  return $fetchAPI<Blob>(`/files/${file.code}`)
    .then(blob => URL.createObjectURL(blob))
}, { immediate: false })

watch(previewOpen, (val) => {
  if (val && status.value !== 'success' && !error.value) execute()
})
</script>

<template>
  <MediaPreview v-model:open="previewOpen" :file="file">
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
    <div v-else-if="!pending && !error" class="pointer-events-auto max-h-full m-auto">
      <video controls class="max-h-full rounded-lg">
        <source :src="data">
      </video>
    </div>
    <div v-else class="text-white flex flex-col items-center justify-center gap-5">
      <i class="i-tabler-photo-exclamation text-5xl" />
      <h1 class="text-2xl font-semibold">
        {{ t('pages.files.preview.failed') }}
      </h1>
    </div>
  </MediaPreview>
</template>

<style lang="scss">
.video {
  max-height: 100%;

  .plyr {
    max-height: 100%;
  }
}
</style>
