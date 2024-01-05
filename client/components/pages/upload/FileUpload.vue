<script setup lang="ts">
import { FetchError } from 'ofetch'

const { t, m } = useI18n()
const toast = useToast()
const uploadFile = useUploadFile()

const files = ref<FileUpload[]>([])
const empty = computed(() => files.value.length === 0)
const hasFileToUpload = computed(() => files.value.some(file => file.status === 'pending'))
const hasCompletedFiles = computed(() => files.value.some(file => file.status === 'completed' || file.status === 'error'))
const { uploadBehavior } = storeToRefs(useUserSettings())
const instantUpload = computed(() => uploadBehavior.value === 'INSTANT')

const { onChange, open } = useFileDialog({ multiple: true })

onChange((uploadedFiles) => {
  if (!uploadedFiles) return
  for (let i = 0; i < uploadedFiles.length; i++) {
    files.value.push({ file: uploadedFiles[i], status: 'pending' })
  }
})

watch(files, (val) => {
  if (val.some(file => file.status === 'pending') && instantUpload.value) upload()
}, { deep: true })

function clear() {
  files.value.splice(0)
}

function clearDone() {
  files.value = files.value.filter(file => file.status !== 'error' && file.status !== 'completed')
}

function uploadSummary(result: UploadStatus[]) {
  const successes = result.filter(status => status === 'completed').length
  const failures = result.filter(status => status === 'error').length

  if (successes === result.length) {
    toast.add({
      severity: 'success',
      summary: t('pages.upload.success.title'),
      detail: { text: t('pages.upload.success.detail', { count: result.length }) },
      life: 5000,
    })
  } else if (failures === result.length) {
    toast.add({
      severity: 'error',
      summary: t('pages.upload.error.title'),
      detail: { text: t('pages.upload.error.detail', { count: result.length }) },
      life: 5000,
    })
  } else {
    toast.add({
      severity: 'error',
      summary: t('pages.upload.partial.title'),
      detail: { text: t('pages.upload.partial.detail', { count: successes, total: result.length }) },
      life: 5000,
    })
  }
}

function upload() {
  if (!hasFileToUpload.value) return

  const uploadPromises = files.value
    .filter(file => file.status === 'pending')
    .map((file) => {
      file.status = 'uploading'
      return uploadFile(file.file)
        .then(() => file.status = 'completed')
        .catch((error) => {
          if (error !== undefined && error instanceof FetchError && error.response) {
            file.statusDetail = m(error.response._data.title)
          }
          file.status = 'error'
        }) as Promise<UploadStatus>
    })

  Promise.all(uploadPromises).then(uploadSummary)
}
</script>

<template>
  <div class="p-card p-5 h-full flex flex-col gap-5">
    <div v-show="!empty" class="flex flex-col lg:flex-row justify-between gap-2">
      <div class="grid grid-cols-1 sm:grid-cols-2 md:flex flex-row gap-2">
        <PButton
          class="sm:grid-col-start-1 sm:grid-col-end-3"
          icon="i-tabler-file-plus"
          :label="t('pages.upload.select_files')"
          outlined
          @click="open()"
        />
        <PButton
          v-show="!instantUpload"
          icon="i-tabler-x"
          :label="t('pages.upload.clear_files')"
          outlined
          @click="clear()"
        />
        <PButton
          icon="i-tabler-clear-all"
          :label="t('pages.upload.clear_done')"
          outlined
          :disabled="!hasCompletedFiles"
          @click="clearDone()"
        />
      </div>
      <PButton
        v-show="!instantUpload"
        icon="i-tabler-upload"
        :label="t('pages.upload.upload_files')"
        :disabled="!hasFileToUpload"
        @click="upload()"
      />
    </div>
    <Dropzone v-model:files="files" />
  </div>
</template>

<style scoped lang="scss">
.controls {
  display: grid;
  grid-template-rows: 0fr;
  transition: grid-template-rows 0.2s ease-in-out;
}

.controls.open {
  grid-template-rows: 1fr;
}
</style>
