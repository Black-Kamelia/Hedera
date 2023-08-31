<script lang="ts" setup>
import type { DynamicDialogInstance } from 'primevue/dynamicdialogoptions'
import type { ComputedRef } from 'vue'

const { t } = useI18n()
const dialog = inject<ComputedRef<DynamicDialogInstance>>('dialogRef')!

const store = useRenameFileDialog()
const { name } = storeToRefs(store)

function cancel() {
  dialog.value.close()
}
function submit() {
  dialog.value.close({ newName: name.value })
}
</script>

<template>
  <PButton :label="t('pages.profile.tokens.create_dialog.cancel')" text @click="cancel()" />
  <PButton :label="t('pages.profile.tokens.create_dialog.submit')" @click="submit()" />
</template>
