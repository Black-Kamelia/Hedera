<script lang="ts" setup>
import { object, string } from 'yup'
import { useEditFileCustomLink } from '~/composables/fileTable/useEditFileCustomLink'

defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()
const editFileCustomLink = useEditFileCustomLink()
const { selectedRow } = useFilesTable()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)

const schema = object({
  customLink: string()
    .required(t('forms.edit_custom_link.errors.missing_slug'))
    .matches(/^[a-z0-9\-]+$/, t('forms.edit_custom_link.errors.invalid_slug')),
})
const { handleSubmit, resetForm, setFieldError } = useForm({
  validationSchema: schema,
})

const link = ref<string>('')
const origin = computed(() => location.origin)
const originalLink = computed(() => `${location.origin}/${selectedRow.value?.code}`)

const submit = handleSubmit((values) => {
  if (!selectedRow.value) return
  pending.value = true

  editFileCustomLink(selectedRow.value?.id, { slug: values.customLink })
    .then(() => visible.value = false)
    .catch((error) => {
      setFieldErrors(error.response._data.fields, setFieldError)
      pending.value = false
    })
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50% min-w-90% md:min-w-35em"
    :header="t('pages.files.edit_custom_link.title')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="resetForm()"
  >
    <div class="flex flex-col gap-3">
      <div class="flex flex-col gap-3 items-start">
        <p class="text-[--text-color-secondary]">
          Votre fichier sera toujours accessible via le lien original :
          <a :href="originalLink" target="_blank" class="inline-flex flew-row items-center gap-0.5">
            {{ originalLink }} <i class="h-1em w-1em i-tabler-external-link" />
          </a>
        </p>

        <div class="flex flex-row w-full items-end dual-inputs">
          <FormInputTextGroup
            id="customLink"
            v-model="link"
            name="customLink"
            :label="t('forms.edit_custom_link.fields.slug')"
            placeholder="lien-personnel-unique"
            :transform-value="slugRestrict"
            class="w-full"
            :start-addons="[`${origin}/:`]"
            autofocus
            @keydown.enter="submit"
          />
        </div>

        <p class="text-[--text-color-secondary]">
          Un lien personnalisé est préfixé par un symbole deux-points (:), contrairement à un lien original qui est préfixé par un symbole dollar ($).
        </p>
      </div>
    </div>

    <template #footer>
      <PButton
        :label="t('forms.edit_custom_link.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('forms.edit_custom_link.submit')"
        :loading="pending"
        icon="i-tabler-check"
        type="submit"
        @click="submit"
      />
    </template>
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
