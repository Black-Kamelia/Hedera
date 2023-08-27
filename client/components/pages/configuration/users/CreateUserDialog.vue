<script lang="ts" setup>
import { boolean, object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import FormDropdown from '~/components/input/FormDropdown.vue'
import { CreateUserForm } from '~/utils/forms'

const { t } = useI18n()
const dev = getRandomDeveloperUsername()
const { user } = useAuth()
const createUser = useCreateUser()
const { refresh } = useUsersTable()
const setFieldErrors = useFormErrors()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)

const roles = [
  { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
  { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
]

function roleDisabled(role: { value: string }) {
  if (user?.role === 'OWNER') return false
  if (user?.role === 'ADMIN' && role.value === 'REGULAR') return false
  return true
}

const schema = object({
  username: string()
    .required(t('forms.create_user.errors.missing_username')),
  password: string()
    .required(t('forms.create_user.errors.missing_password'))
    .min(CreateUserForm.username.min, t('forms.create_user.errors.password_too_short', { min: CreateUserForm.username.min }))
    .max(CreateUserForm.username.max, t('forms.create_user.errors.password_too_long', { max: CreateUserForm.username.max })),
  confirmPassword: string()
    .required(t('forms.create_user.errors.missing_confirm_password'))
    .oneOf([yref('password')], t('forms.create_user.errors.passwords_mismatch')),
  email: string()
    .required(t('forms.create_user.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),
  role: string()
    .required(t('forms.create_user.errors.missing_role')),
  forceChangePassword: boolean()
    .required(t('forms.create_user.errors.missing_force_change_password')),
})
const { handleSubmit, resetForm, setFieldError } = useForm({
  validationSchema: schema,
  initialValues: {
    forceChangePassword: true,
  },
})

const submit = handleSubmit(async (values) => {
  pending.value = true
  createUser(values as UserCreationDTO)
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

function onHide() {
  resetForm()
  pending.value = false
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50%"
    :header="t('pages.configuration.users.create_dialog.title')"
    :draggable="false"
    :dismissable-mask="true"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="flex flex-col gap-3">
      <p class="text-[--text-color-secondary] mb-3">
        {{ t('pages.configuration.users.create_dialog.summary') }}
      </p>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-3 items-start">
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

        <FormInputText
          id="password"
          name="password"
          :label="t('forms.create_user.fields.password')"
          placeholder="••••••••••••••••"
          type="password"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormInputText
          id="confirmPassword"
          name="confirmPassword"
          :label="t('forms.create_user.fields.confirm_password')"
          placeholder="••••••••••••••••"
          type="password"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormDropdown
          id="role"
          name="role"
          :label="t('forms.create_user.fields.role')"
          :placeholder="t('forms.create_user.fields.role_placeholder')"
          :options="roles"
          option-label="label"
          option-value="value"
          :option-disabled="roleDisabled"
          class="w-full"
          @keydown.enter="submit"
        />
      </div>

      <div class="mt-3">
        <FormCheckbox
          id="forceChangePassword"
          name="forceChangePassword"
          :label="t('forms.create_user.fields.force_change_password')"
          binary
        />
      </div>
    </div>

    <template #footer>
      <PButton
        :label="t('pages.configuration.users.create_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.configuration.users.create_dialog.submit')"
        :loading="pending"
        type="submit"
        @click="submit"
      />
    </template>
  </PDialog>
</template>
