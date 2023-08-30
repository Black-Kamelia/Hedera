<script setup lang="ts">
import type { FileUploadUploaderEvent } from 'primevue/fileupload'
import { PFileUpload } from '#components'

const { t } = useI18n()
const toast = useToast()

const uploadFile = useUploadFile()
const instantUpload = ref(false) // TODO: persist settings

const uploadingFiles = ref<File[]>([])
const uploadedFiles = ref<File[]>([])
const erroredFiles = ref<File[]>([])
const hasFiles = computed(() => uploadingFiles.value.length > 0 || uploadedFiles.value.length > 0 || erroredFiles.value.length > 0)

async function uploader(event: FileUploadUploaderEvent) {
  const files = event.files instanceof File ? [event.files] : event.files
  uploadingFiles.value.push(...files)

  const uploadPromises = files.map(file => uploadFile(file)
    .then(() => uploadedFiles.value.push(file))
    .catch(() => erroredFiles.value.push(file))
    .finally(() => uploadingFiles.value.splice(uploadingFiles.value.indexOf(file), 1)))

  await Promise.all(uploadPromises)

  toast.add({
    severity: 'info',
    summary: t('pages.upload.finished'),
    life: 5000,
  })
}
</script>

<template>
  <div class="w-full">
    <PFileUpload
      multiple
      :auto="instantUpload"
      :show-upload-button="!instantUpload"
      :show-cancel-button="!instantUpload"
      :choose-label="t('pages.upload.select_files')"
      :upload-label="t('pages.upload.upload_files')"
      :cancel-label="t('pages.upload.clear_files')"
      choose-icon="i-tabler-file-plus"
      upload-icon="i-tabler-cloud-upload"
      cancel-icon="i-tabler-x"
      custom-upload
      :pt="{
        root: { class: 'max-h-full' },
        buttonbar: { class: 'important-border-none' },
        content: { class: 'important-border-none' },
      }"
      @uploader="uploader"
    >
      <template #content="{ files: pendingFiles, removeFileCallback }">
        <FileDropFilesSection :files="pendingFiles" status="pending" @remove="removeFileCallback" />
        <FileDropFilesSection :files="uploadingFiles" status="uploading" />
        <FileDropFilesSection :files="uploadedFiles" status="completed" />
        <FileDropFilesSection :files="erroredFiles" status="error" />
      </template>

      <template #empty>
        <div v-if="!hasFiles" class="flex items-center justify-center flex-col">
          <i class="i-tabler-cloud-upload text-8xl" />
          <p class="mt-4 mb-0">
            {{ t("pages.upload.drag_n_drop") }}
          </p>
        </div>
      </template>
    </PFileUpload>
  </div>
</template>
