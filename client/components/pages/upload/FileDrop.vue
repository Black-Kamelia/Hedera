<script setup lang="ts">
import type { FileUploadUploaderEvent } from 'primevue/fileupload'

const { t } = useI18n()
const toast = useToast()
const { uploadBehavior } = storeToRefs(useUserSettings())

const uploadFile = useUploadFile()
const instantUpload = computed(() => uploadBehavior.value === 'INSTANT')

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
      custom-upload
      :pt="{
        buttonbar: { class: 'important-border-none important-bg-[--surface-overlay]' },
        content: { class: 'important-border-none flex-grow' },
        //empty: { class: 'h-full' },
        root: { class: 'flex flex-col h-full max-h-full' },
      }"
      @uploader="uploader"
    >
      <template #header="{ chooseCallback, clearCallback, uploadCallback, files }">
        <div class="flex flex-row justify-between w-full">
          <div class="flex flex-row gap-3">
            <PButton icon="i-tabler-file-plus" :label="t('pages.upload.select_files')" @click="chooseCallback" />
            <PButton v-if="!instantUpload" icon="i-tabler-x" :label="t('pages.upload.clear_files')" :disabled="files.length === 0" @click="clearCallback" />
          </div>
          <div class="flex flex-row gap-3">
            <PButton v-if="!instantUpload" icon="i-tabler-upload" :label="t('pages.upload.upload_files')" :disabled="files.length === 0" @click="uploadCallback" />
          </div>
        </div>
      </template>

      <template #content="{ files: pendingFiles, removeFileCallback }">
        <FileDropFilesSection :files="pendingFiles" status="pending" @remove="removeFileCallback" />
        <FileDropFilesSection :files="uploadingFiles" status="uploading" />
        <FileDropFilesSection :files="uploadedFiles" status="completed" />
        <FileDropFilesSection :files="erroredFiles" status="error" />
      </template>

      <template #empty>
        <div v-if="!hasFiles" class="b-2 b-[--surface-border] b-dashed rounded-lg p-10 flex items-center justify-center flex-col h-full">
          <i class="i-tabler-cloud-upload text-8xl" />
          <p class="mt-4 mb-0">
            {{ t("pages.upload.drag_n_drop") }}
          </p>
        </div>
      </template>
    </PFileUpload>
  </div>
</template>
