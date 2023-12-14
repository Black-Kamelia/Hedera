<script lang="ts" setup>
import { object, string } from 'yup'

const { selection } = defineProps<{
  selection: FileRepresentationDTO[]
}>()

const { t } = useI18n()
const bulkEditFileVisibility = useBulkEditVisibility()

const visible = defineModel<boolean>('visible', { default: false })
const loading = ref(false)

const options = computed(() => [
  { icon: 'i-tabler-world', label: t('pages.files.visibility.public'), value: 'PUBLIC' },
  { icon: 'i-tabler-link', label: t('pages.files.visibility.unlisted'), value: 'UNLISTED' },
  { icon: 'i-tabler-eye-off', label: t('pages.files.visibility.private'), value: 'PRIVATE' },
])

const schema = object({
  visibility: string()
    .required(t('forms.bulk_change_visibility.errors.missing_visibility')),
})
const { handleSubmit, resetForm } = useForm({
  validationSchema: schema,
})

const submit = handleSubmit((values) => {
  loading.value = true

  bulkEditFileVisibility(selection.map(file => file.id), values.visibility)
    .finally(() => {
      visible.value = false
      loading.value = false
    })
})

function onHide() {
  resetForm()
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="w-100% sm:w-35em"
    :header="t('pages.files.bulk_actions.edit_visibility.header')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <form @submit="submit">
      <div class="flex flex-col gap-3">
        <div class="flex flex-col gap-3 items-start">
          <p class="text-[--text-color-secondary]">
            {{ t('pages.files.bulk_actions.edit_visibility.description', { count: selection.length }) }}
          </p>

          <FormDropdown
            id="visibility"
            class="w-full"
            name="visibility"
            :label="t('forms.bulk_change_visibility.fields.visibility')"
            :placeholder="t('forms.bulk_change_visibility.fields.visibility_placeholder')"
            option-label="label"
            option-value="value"
            :options="options"
          />
        </div>
      </div>

      <div class="flex flex-row justify-end gap-3 pt-6">
        <PButton
          :label="t('forms.bulk_change_visibility.cancel')"
          text
          :disabled="loading"
          @click="visible = false"
        />
        <PButton
          :label="t('forms.bulk_change_visibility.submit')"
          :loading="loading"
          icon="i-tabler-check"
          type="submit"
          @click="submit"
        />
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
