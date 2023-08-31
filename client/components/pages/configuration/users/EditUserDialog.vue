<script lang="ts" setup>
import { object, string } from 'yup'
import FormDropdown from '~/components/input/FormDropdown.vue'

defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const dev = getRandomDeveloperUsername()
const { user } = useAuth()
const { selectedRow, refresh } = useUsersTable()
const updateUser = useUpdateUser()
const setFieldErrors = useFeedbackFormErrors()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)

const roles = computed(() => selectedRow.value?.role === 'OWNER'
  ? [
      { label: t('pages.configuration.users.role.owner'), value: 'OWNER', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
    ]
  : [
      { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
    ],
)

const schema = object({
  username: string()
    .required(t('forms.create_user.errors.missing_username')),
  email: string()
    .required(t('forms.create_user.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),
  role: string()
    .required(t('forms.create_user.errors.missing_role')),
})
const { handleSubmit, resetForm, setValues, setFieldError } = useForm({
  validationSchema: schema,
})

const submit = handleSubmit(async (values) => {
  if (!selectedRow.value) return

  pending.value = true
  updateUser(selectedRow.value.id, values as UserCreationDTO)
    .then(() => {
      visible.value = false
      refresh()
    })
    .catch((err) => {
      setFieldErrors(err.response._data.fields, setFieldError)
    })
    .finally(() => {
      pending.value = false
    })
})

watch(visible, (val) => {
  if (val) {
    if (!selectedRow.value) return
    setValues({
      username: selectedRow.value.username,
      email: selectedRow.value.email,
      role: selectedRow.value.role,
    })
  }
})

function onHide() {
  resetForm()
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50% min-w-90% md:min-w-35em"
    :header="t('pages.configuration.users.edit_dialog.title')"
    :draggable="false"
    :dismissable-mask="true"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="flex flex-col gap-3">
      <PMessage
        severity="info"
        class="mb-3"
        :closable="false" icon="i-tabler-info-circle-filled"
        :pt="{ root: { style: { marginTop: 0 } } }"
      >
        {{ t('pages.configuration.users.edit_dialog.password_summary') }}
      </PMessage>

      <div class="grid grid-cols-1 gap-3 items-start">
        <FormInputText
          id="username"
          name="username"
          :label="t('forms.create_user.fields.username')"
          :placeholder="dev"
          :transform-value="usernameRestrict"
          class="w-full"
          autofocus
          @keydown.enter="submit"
        />

        <FormInputText
          id="email"
          name="email"
          :label="t('forms.create_user.fields.email')"
          :placeholder="`${dev}@example.com`"
          type="email"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormDropdown
          id="role"
          name="role"
          :label="t('forms.create_user.fields.role')"
          :placeholder="t('forms.create_user.fields.role_placeholder')"
          :options="roles"
          :disabled="selectedRow?.role === 'OWNER' || user?.role !== 'OWNER'"
          option-label="label"
          option-value="value"
          class="w-full"
          @keydown.enter="submit"
        />
      </div>
    </div>

    <template #footer>
      <PButton
        :label="t('pages.configuration.users.edit_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.configuration.users.edit_dialog.submit')"
        :loading="pending"
        icon="i-tabler-check"
        type="submit"
        @click="submit"
      />
    </template>
  </PDialog>
</template>
