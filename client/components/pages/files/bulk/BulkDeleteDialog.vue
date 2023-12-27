<script lang="ts" setup>
const selection = defineModel<FileRepresentationDTO[]>('selection', { default: () => [] })

const { t } = useI18n()
const bulkDeleteFiles = useBulkDelete()
const { refresh } = useFilesTable()

const visible = defineModel<boolean>('visible', { default: false })
const loading = ref(false)

function submit() {
  loading.value = true

  bulkDeleteFiles(selection.value.map(file => file.id))
    .then(() => selection.value = [])
    .then(refresh)
    .finally(() => {
      visible.value = false
      loading.value = false
    })
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="w-100% sm:w-35em"
    :header="t('pages.files.bulk_actions.delete.header')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
  >
    <div class="flex flex-col gap-3">
      <div class="flex flex-col gap-3 items-start">
        <p class="text-[--text-color-secondary]">
          {{ t('pages.files.bulk_actions.delete.description', { count: selection.length }) }}
        </p>
      </div>
    </div>

    <div class="flex flex-row justify-end gap-3 pt-6">
      <PButton
        :label="t('pages.files.bulk_actions.delete.cancel')"
        text
        :disabled="loading"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.files.bulk_actions.delete.submit')"
        :loading="loading"
        icon="i-tabler-trash"
        severity="danger"
        type="submit"
        @click="submit"
      />
    </div>
  </PDialog>
</template>

<style scoped lang="scss">
a {
  color: var(--primary-color);

  &:hover {
    text-decoration: underline;
  }
}

.dual-inputs {
  .p-inputtext {
    display: none !important;
  }
}
</style>
