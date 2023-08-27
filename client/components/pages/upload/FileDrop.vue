<script setup lang="ts">
import type { FileUploadRemoveEvent, FileUploadUploadEvent } from 'primevue/fileupload'
import { PFileUpload } from '#components'

const { t } = useI18n()

const uploadFile = useUploadFile()
const instantUpload = ref(true) // TODO: implement user setting

const pendingFiles = ref<File[]>([])
const uploadedFiles = ref<File[]>([])

async function doUpload() {
  const uploadPromises = pendingFiles.value.map(file => uploadFile(file))
  await Promise.all(uploadPromises)
  uploadedFiles.value = [...uploadedFiles.value, ...pendingFiles.value]
  pendingFiles.value = []
}

function onClear() {
  pendingFiles.value = []
  uploadedFiles.value = []
}

function onRemove(files: FileUploadRemoveEvent) {
  const removedFiles = files.files as File[]
  pendingFiles.value = pendingFiles.value.filter(file => !removedFiles.includes(file))
}

async function onUpdate(event: FileUploadUploadEvent) {
  const files = event.files as File[]
  pendingFiles.value = files

  if (instantUpload.value) {
    await doUpload()
  }
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
      @upload="doUpload"
      @select="onUpdate"
      @clear="onClear"
      @remove="onRemove"
      @before-upload="null"
      @before-send="null"
    >
      <template
        #content="{
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
