<script lang="ts" setup>
import { object, string } from 'yup'
import { useEditFileName } from '~/composables/fileTable/useChangeFileName'
import { UPDATE_FILE_FORM } from '~/utils/forms'

defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()
const editFileName = useEditFileName()
const { selectedRow, updateSelectedRow, unselectRow } = useFilesTable()

const visible = defineModel<boolean>('visible', { default: false })
const savePending = ref(false)

const schema = object({
  filename: string()
    .required(t('forms.rename_file.errors.missing_name'))
    .max(UPDATE_FILE_FORM.filename.max, t('forms.rename_file.errors.name_too_long', { max: UPDATE_FILE_FORM.filename.max })),
})
const { handleSubmit, resetForm, setFieldValue, setFieldError } = useForm({
  validationSchema: schema,
})

const filename = ref('')

const submit = handleSubmit((values) => {
  if (!selectedRow.value) return
  savePending.value = true

  if (values.filename === selectedRow.value.name) {
    visible.value = false
    savePending.value = false
    return
  }

  editFileName(selectedRow.value?.id, values.filename)
    .then((response) => {
      if (response) {
        visible.value = false
        updateSelectedRow(response.payload!)
      }
    })
    .catch((error) => {
      setFieldErrors(error.response._data.fields, setFieldError)
      savePending.value = false
    })
    .finally(() => savePending.value = false)
})

function onHide() {
  resetForm()
  unselectRow()
}

watch(visible, (val) => {
  if (val) {
    if (!selectedRow.value) return

    setFieldValue('filename', selectedRow.value.name, false)
  }
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="w-100% sm:w-35em"
    :header="t('pages.files.rename.title')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="flex flex-col gap-3">
      <div class="flex flex-col gap-3 items-start">
        <p class="text-[--text-color-secondary]">
          {{ t('pages.files.rename.summary') }}
        </p>

        <FormInputText
          v-model="filename"
          name="filename"
          :label="t('forms.rename_file.fields.filename')"
          class="w-full mt-0.5"
          autofocus
          @keydown.enter="submit"
        />
      </div>
    </div>

    <div class="flex flex-row justify-end gap-3 pt-6">
      <PButton
        :label="t('forms.rename_file.cancel')"
        text
        :disabled="savePending"
        @click="visible = false"
      />
      <PButton
        :label="t('forms.rename_file.submit')"
        :loading="savePending"
        icon="i-tabler-check"
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
