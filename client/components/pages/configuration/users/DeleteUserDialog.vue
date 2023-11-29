<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'

const { t } = useI18n()
const { selectedRow, refresh } = useUsersTable()
const deleteUser = useDeleteUser()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)

const schema = object({
  targetUsername: string(),
  username: string()
    .required(t('forms.delete_user.errors.missing_username'))
    .oneOf([yref('targetUsername')], t('forms.delete_user.errors.username_mismatch')),
})
const { handleSubmit, resetForm, setValues, values } = useForm({
  validationSchema: schema,
})

const username = computed(() => selectedRow.value?.username ?? '')
const usernamesMatch = computed(() => values.username === username.value)

const submit = handleSubmit(async () => {
  if (!selectedRow.value) return
  pending.value = true

  deleteUser(selectedRow.value.id)
    .then(() => {
      visible.value = false
      refresh()
    })
    .finally(() => {
      pending.value = false
    })
})

watch(visible, (val) => {
  if (!val) return
  if (!selectedRow.value) return
  setValues({ targetUsername: selectedRow.value.username })
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="w-35em"
    :header="t('pages.configuration.users.delete_dialog.title')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="resetForm()"
  >
    <div class="flex flex-col gap-3">
      <i18n-t
        keypath="pages.configuration.users.delete_dialog.summary"
        class="color-[--text-color-secondary]"
        scope="global"
        tag="p"
      >
        <template #username>
          <b class="color-[--text-color]">{{ username }}</b>
        </template>
      </i18n-t>

      <i18n-t
        keypath="pages.configuration.users.delete_dialog.input"
        class="color-[--text-color-secondary]"
        scope="global"
        tag="p"
      >
        <template #username>
          <b class="color-[--text-color]">{{ username }}</b>
        </template>
      </i18n-t>

      <FormInputText
        id="deleteUser_username"
        name="username"
        label=""
        class="w-full"
        :disabled="pending"
        autocomplete="off"
        autofocus
        @keydown.enter="submit"
        @paste.prevent
      />
    </div>

    <template #footer>
      <PButton
        :label="t('pages.configuration.users.delete_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.configuration.users.delete_dialog.submit')"
        :loading="pending"
        icon="i-tabler-trash"
        severity="danger"
        type="submit"
        :disabled="!usernamesMatch"
        @click="submit"
      />
    </template>
  </PDialog>
</template>
