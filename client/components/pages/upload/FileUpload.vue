<script setup lang="ts">
const { t } = useI18n()

const files = ref<File[]>([])
const empty = computed(() => files.value.length === 0)

const { onChange, open } = useFileDialog({ multiple: true })

onChange((uploadedFiles) => {
  if (!uploadedFiles) return
  for (let i = 0; i < uploadedFiles.length; i++) {
    files.value.push(uploadedFiles[i])
  }
})

function clear() {
  files.value.splice(0)
}
</script>

<template>
  <div class="p-card p-5 h-full flex flex-col gap-5">
    <div v-show="!empty" class="flex flex-row gap-2">
      <PButton
        icon="i-tabler-file-plus"
        :label="t('pages.upload.select_files')"
        outlined
        @click="open()"
      />
      <PButton
        icon="i-tabler-x"
        :label="t('pages.upload.clear_files')"
        outlined
        @click="clear()"
      />
      <div class="flex-grow" />
      <PButton
        icon="i-tabler-upload"
        :label="t('pages.upload.upload_files')"
      />
    </div>
    <Dropzone :files="files" />
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
