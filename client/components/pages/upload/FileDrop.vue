<script setup lang="ts">
import type { FileUploadUploadEvent } from 'primevue/fileupload'

const { t } = useI18n()
const toast = useToast()
const { format } = useHumanFileSize()

const files = ref<File[]>([])
const totalSize = computed(() => files.value.reduce((acc, file) => acc + file.size, 0))
const areFilesChosen = computed(() => files.value.length !== 0)

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
      @upload="onUpload"
      @select="onUpdate"
      @clear="onUpdate({ files: [] })"
      @remove="onUpdate"
    >
      <template #content="{ files: pendingFiles, uploadedFiles, removeUploadedFileCallback, removeFileCallback }">
        <div class="flex flex-wrap p-0 sm:p-5 gap-5">
          <FileDropFilesSection :files="pendingFiles" :completed="false" :remove-callback="removeFileCallback" />
          <FileDropFilesSection :files="uploadedFiles" :completed="true" :remove-callback="removeUploadedFileCallback" />
        </div>
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
