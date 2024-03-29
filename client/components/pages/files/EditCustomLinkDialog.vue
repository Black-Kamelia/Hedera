<script lang="ts" setup>
import { object, string } from 'yup'

defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()
const { editCustomLink, deleteCustomLink } = useEditFileCustomLink()
const { selectedRow, updateSelectedRow, unselectRow } = useFilesTable()

const visible = defineModel<boolean>('visible', { default: false })
const header = computed(() => {
  if (selectedRow?.value?.customLink) return t('pages.files.edit_custom_link.title_edit')
  return t('pages.files.edit_custom_link.title_set')
})
const savePending = ref(false)
const removePending = ref(false)

const schema = object({
  customLink: string()
    .required(t('forms.edit_custom_link.errors.missing_slug'))
    .matches(/^[a-z0-9]+(-[a-z0-9]+)*$/, t('forms.edit_custom_link.errors.invalid_slug')),
})
const { handleSubmit, resetForm, setFieldValue, setFieldError } = useForm({
  validationSchema: schema,
})

const link = ref<string>('')
const origin = computed(() => location.origin)
const originalLink = computed(() => `${location.origin}/m/${selectedRow.value?.code}`)

const submit = handleSubmit((values) => {
  if (!selectedRow.value) return
  savePending.value = true

  editCustomLink(selectedRow.value?.id, values.customLink)
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

function removeLink() {
  if (!selectedRow.value) return
  removePending.value = true

  deleteCustomLink(selectedRow.value?.id)
    .then((response) => {
      if (response) {
        visible.value = false
        updateSelectedRow({ ...response.payload!, customLink: null })
      }
    })
    .catch((error) => {
      setFieldErrors(error.response._data.fields, setFieldError)
      removePending.value = false
    })
    .finally(() => removePending.value = false)
}

function onHide() {
  resetForm()
  unselectRow()
}

watch(visible, (val) => {
  if (val) {
    if (!selectedRow.value) return

    setFieldValue('customLink', selectedRow.value.customLink, false)
  }
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50% min-w-90% md:min-w-35em"
    :header="header"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <form @submit="submit">
      <div class="flex flex-col gap-3 items-start">
        <i18n-t
          keypath="pages.files.edit_custom_link.old_link_summary"
          class="text-[--text-color-secondary]"
          scope="global"
          tag="p"
        >
          <template #link>
            <a :href="originalLink" target="_blank" class="inline-flex flex-row items-center gap-0.5">
              {{ originalLink }} <i class="h-1em w-1em i-tabler-external-link" />
            </a>
          </template>
        </i18n-t>

        <div class="flex flex-row w-full items-end dual-inputs">
          <FormInputTextGroup
            id="customLink"
            v-model="link"
            name="customLink"
            :label="t('forms.edit_custom_link.fields.slug')"
            :placeholder="t('forms.edit_custom_link.fields.slug_placeholder')"
            :transform-value="slugRestrict"
            class="flex-grow"
            :start-addons="[`${origin}/c/`]"
            autofocus
          />
        </div>

        <i18n-t
          keypath="pages.files.edit_custom_link.links_summary"
          class="text-[--text-color-secondary]"
          scope="global"
          tag="p"
        >
          <template #c_route>
            <PTag style="background-color: var(--surface-border); color: var(--text-color-secondary)">
              /c/
            </PTag>
          </template>
          <template #i_route>
            <PTag style="background-color: var(--surface-border); color: var(--text-color-secondary)">
              /m/
            </PTag>
          </template>
        </i18n-t>
      </div>

      <div class="flex flex-row justify-between gap-2 pt-9 items-center">
        <PButton
          :label="t('forms.edit_custom_link.remove_link')"
          text
          :loading="removePending"
          :disabled="savePending || !selectedRow?.customLink"
          @click="removeLink"
        />
        <div class="flex flex-row gap-3">
          <PButton
            :label="t('forms.edit_custom_link.cancel')"
            text
            :disabled="savePending || removePending"
            @click="visible = false"
          />
          <PButton
            :label="t('forms.edit_custom_link.submit')"
            :loading="savePending"
            :disabled="removePending"
            icon="i-tabler-check"
            type="submit"
            @click="submit"
          />
        </div>
      </div>
    </form>
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
