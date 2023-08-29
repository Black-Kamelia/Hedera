<script setup lang="ts">
import type { FileUploadUploadEvent } from 'primevue/fileupload'
import { PFileUpload } from '#components'

const { t } = useI18n()

const uploadFile = useUploadFile()
const instantUpload = ref(true) // TODO: implement user setting

async function onUpload(event: FileUploadUploadEvent) {
  const files = event.files instanceof File ? [event.files] : event.files
  const uploadPromises = files.map(file => uploadFile(file))
  await Promise.all(uploadPromises)
}
</script>

<template>
  <div class="w-full">
    <PFileUpload
      :auto="instantUpload"
      :show-upload-button="!instantUpload"
      :multiple="true"
      :choose-label="t('pages.upload.select_files')"
      :upload-label="t('pages.upload.upload_files')"
      :cancel-label="t('pages.upload.clear_files')"
      choose-icon="i-tabler-file-plus"
      upload-icon="i-tabler-cloud-upload"
      cancel-icon="i-tabler-x"
      @upload="onUpload"
    >
      <template
        #content="{
          files: pendingFiles,
          uploadedFiles,
          removeFileCallback,
          removeUploadedFileCallback,
        }"
      >
        <FileDropFilesSection :files="pendingFiles" :completed="false" @remove="removeFileCallback" />
        <FileDropFilesSection :files="uploadedFiles" :completed="true" @remove="removeUploadedFileCallback" />
      </template>

      <template #empty>
        <div class="flex items-center justify-center flex-col">
          <i class="i-tabler-cloud-upload text-8xl" />
          <p class="mt-4 mb-0">
            {{ t("pages.upload.drag_n_drop") }}
          </p>
        </div>
      </template>
    </PFileUpload>
  </div>
</template>
