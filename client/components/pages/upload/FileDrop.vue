<script setup lang="ts">
import type { FileUploadUploadEvent } from 'primevue/fileupload'

const { t } = useI18n()
const toast = useToast()

const files = ref<File[]>([])

function onUpdate(event: { files: File[] }) {
  files.value = event.files
}

function onUpload(event: FileUploadUploadEvent) {
  const filesToUpload = event.files
  toast.add({ severity: 'info', summary: 'Success', detail: 'File Uploaded', life: 3000 })
}
</script>

<template>
  <div class="w-full">
    <PFileUpload
      :multiple="true"
      :choose-label="t('pages.upload.select_files')"
      :upload-label="t('pages.upload.upload_files')"
      :cancel-label="t('pages.upload.clear_files')"
      choose-icon="i-tabler-file-plus"
      upload-icon="i-tabler-cloud-upload"
      cancel-icon="i-tabler-x"
      @upload="onUpload"
      @select="onUpdate"
      @clear="onUpdate({ files: [] })"
      @remove="onUpdate"
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
