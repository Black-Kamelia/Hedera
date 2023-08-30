<script setup lang="ts">
import type { FileUploadUploaderEvent } from 'primevue/fileupload'
import { PFileUpload } from '#components'

const { t } = useI18n()

const uploadFile = useUploadFile()
const instantUpload = ref(false) // TODO: persist settings

const uploadingFiles = ref<File[]>([])
const uploadedFiles = ref<File[]>([])

async function uploader(event: FileUploadUploaderEvent) {
  const files = event.files instanceof File ? [event.files] : event.files
  uploadingFiles.value.push(...files)
  const uploadPromises = files.map(file => uploadFile(file).then(() => {
    uploadingFiles.value = uploadingFiles.value.filter(f => f.name + f.type + f.size !== file.name + file.type + file.size)
    uploadedFiles.value.push(file)
  }))
  await Promise.all(uploadPromises)
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
      @uploader="uploader"
    >
      <template
        #content="{
          files: pendingFiles,
          removeFileCallback,
        }"
      >
        <FileDropFilesSection :files="pendingFiles" status="pending" @remove="removeFileCallback" />
        <FileDropFilesSection :files="uploadingFiles" status="uploading" />
        <FileDropFilesSection :files="uploadedFiles" status="completed" />
      </template>

      <template #empty>
        <div v-if="uploadedFiles.length === 0" class="flex items-center justify-center flex-col">
          <i class="i-tabler-cloud-upload text-8xl" />
          <p class="mt-4 mb-0">
            {{ t("pages.upload.drag_n_drop") }}
          </p>
        </div>
      </template>
    </PFileUpload>
  </div>
</template>
