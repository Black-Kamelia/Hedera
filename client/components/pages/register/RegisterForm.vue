<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import { CREATE_USER_FORM } from '~/utils/forms'

const { t, m } = useI18n()
const { login } = useAuth()

const usernamePlaceholder = getRandomDeveloperUsername()
const message = defineModel<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>('message', {
  default: {
    content: null,
    severity: undefined,
  },
})

const loading = ref(false)
const usernameField = ref<Nullable<CompElement>>(null)
const emailField = ref<Nullable<CompElement>>(null)
const passwordField = ref<Nullable<CompElement>>(null)
const confirmPasswordField = ref<Nullable<CompElement>>(null)

const schema = object({
  username: string()
    .required(t('forms.register.errors.missing_username'))
    .matches(/^[a-z0-9_\-.]+$/, t('forms.register.errors.invalid_username')),
  email: string()
    .required(t('forms.register.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),
  password: string()
    .required(t('forms.register.errors.missing_password'))
    .min(CREATE_USER_FORM.password.min, t('forms.register.errors.password_too_short', { min: CREATE_USER_FORM.password.min }))
    .max(CREATE_USER_FORM.password.max, t('forms.register.errors.password_too_long', { max: CREATE_USER_FORM.password.max })),
  confirmPassword: string()
    .required(t('forms.register.errors.missing_password_confirmation'))
    .oneOf([yref('password')], t('forms.register.errors.passwords_mismatch')),

})
const { handleSubmit, resetField } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit((values) => {
  loading.value = true
  login(values).catch(() => loading.value = false)
})

function hideErrorMessage() {
  message.value.content = null
}

useEventBus(LoggedInEvent).on((event) => {
  if (event.error) {
    const status = event.error?.response?.status

    if (status === 500) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
      message.value.content = t('forms.register.errors.server_error')
      message.value.severity = 'error'
      return
    }

    if (status === 401) {
      resetField('password')
      passwordField.value?.$el.focus()
    }
    if (status === 403) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
    }

    message.value.content = m(event.error.data.title)
    message.value.severity = 'error'
  }
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage
      v-show="message.content" :pt="{ root: { class: 'important-mt-0' } }" :severity="message.severity"
      icon="i-tabler-alert-circle-filled" :closable="false"
    >
      {{ message.content }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="username"
        ref="usernameField"
        class="w-full"
        name="username"
        type="text"
        :label="t('forms.register.fields.username')"
        :placeholder="usernamePlaceholder"
        :transform-value="usernameRestrict"
        start-icon="i-tabler-user"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="email"
        ref="emailField"
        class="w-full"
        name="email"
        type="email"
        :label="t('forms.register.fields.email')"
        :placeholder="`${usernamePlaceholder}@example.com`"
        start-icon="i-tabler-mail"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="password"
        ref="passwordField"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.register.fields.password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="confirmPassword"
        ref="confirmPasswordField"
        class="w-full"
        name="confirmPassword"
        type="password"
        :label="t('forms.register.fields.confirm_password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        @input="hideErrorMessage"
      />
    </div>

    <div class="flex flex-row items-center justify-end mb-6 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.register.back') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.register.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
