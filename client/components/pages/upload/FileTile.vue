<script setup lang="ts">
const { file, status } = defineProps<{
  file: File
  status: UploadStatus
}>()

const emit = defineEmits<{
  (event: 'remove'): void
}>()

const { t } = useI18n()
const { format } = useHumanFileSize()

const thumbnail = URL.createObjectURL(file)
onBeforeUnmount(() => URL.revokeObjectURL(thumbnail))
</script>

<template>
  <div class="tile">
    <div class="relative">
      <Thumbnail
        :src="thumbnail"
        :alt="file.name"
        :type="file.type"
        :previewable="false"
        height="10em"
        width="15em"
        border-radius="0"
      />

      <PTag
        class="absolute bottom-2 left-3 z-100"
        :severity="{
          pending: 'warning',
          uploading: 'warning',
          completed: 'success',
          error: 'danger',
        }[status]"
        :icon="{
          pending: 'i-tabler-clock',
          uploading: 'i-tabler-upload',
          completed: 'i-tabler-check',
          error: 'i-tabler-x',
        }[status]"
        :value="t({
          pending: 'pages.upload.status.pending',
          uploading: 'pages.upload.status.uploading',
          completed: 'pages.upload.status.completed',
          error: 'pages.upload.status.error',
        }[status])"
        rounded
      />
    </div>

    <div class="py-2 px-3 b-t-1 b-t-solid b-t-[var(--surface-border)] footer">
      <div>
        <p :title="file.name" class="text-truncate">
          {{ file.name }}
        </p>
        <p class="color-[var(--text-color-secondary)] text-sm">
          {{ format(file.size) }}
        </p>
      </div>
      <PButton
        v-if="status === 'pending'"
        class="ml-3 delete-btn "
        icon="i-tabler-x"
        rounded
        outlined
        :pt="{ icon: { class: 'w-5 h-5' } }"
        @click="emit('remove')"
      />
      <PProgressSpinner
        v-else-if="status === 'uploading'"
        class="ml-3"
        :style="{ width: '1.5em', height: '1.5em' }"
        stroke-width="4"
      />
    </div>
  </div>
</template>

<style scoped lang="scss">
.tile {
  display: flex;
  position: relative;
  border-radius: .5em;
  flex-direction: column;
  width: 15em;
  border: 1px solid var(--surface-border);
  box-sizing: content-box;
  overflow: hidden;
  z-index: 2;
}

.delete-btn {
  width: 2em;
  height: 2em;
}

.footer {
  display: grid;
  align-items: center;
  grid-template-columns: minmax(0, 1fr) auto;
  width: 100%;
  background: var(--surface-overlay);
}
</style>
