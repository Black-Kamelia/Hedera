<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import { CREATE_USER_FORM } from '~/utils/forms'

const state = defineModel('state')

const { t } = useI18n()
const { login } = useAuth()
const setFieldErrors = useFeedbackFormErrors()

const usernamePlaceholder = getRandomDeveloperUsername()
const loading = ref(false)

const schema = object({
  username: string()
    .required(t('forms.register.errors.missing_username'))
    .min(CREATE_USER_FORM.username.min, t('forms.register.errors.username_too_short', { min: CREATE_USER_FORM.username.min }))
    .max(CREATE_USER_FORM.username.max, t('forms.register.errors.username_too_long', { max: CREATE_USER_FORM.username.max }))
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
const { handleSubmit, setFieldError } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  $fetchAPI<UserSignupDTO>('/users/signup', { method: 'POST', body: values })
    .then(() => login(values))
    .catch((err) => {
      setFieldErrors(err.response._data.fields, setFieldError)
      loading.value = false
    })
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage :pt="{ root: { class: 'important-mt-0' } }" severity="info" icon="i-tabler-info-circle-filled" :closable="false">
      <pre>TODO: Account space quota</pre>
    </PMessage>

    <div class="grid grid-cols-2 gap-3 mb-3">
      <FormInputText
        id="username"
        class="w-full"
        name="username"
        type="text"
        :label="t('forms.register.fields.username')"
        :placeholder="usernamePlaceholder"
        :transform-value="usernameRestrict"
        start-icon="i-tabler-user"
        autocomplete="username"
      />

      <FormInputText
        id="email"
        class="w-full"
        name="email"
        type="email"
        :label="t('forms.register.fields.email')"
        :placeholder="`${usernamePlaceholder}@example.com`"
        start-icon="i-tabler-mail"
        autocomplete="email"
      />

      <FormInputText
        id="password"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.register.fields.password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        autocomplete="new-password"
      />

      <FormInputText
        id="confirmPassword"
        class="w-full"
        name="confirmPassword"
        type="password"
        :label="t('forms.register.fields.confirm_password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        autocomplete="new-password"
      />
    </div>

    <div class="flex flex-row items-center justify-end mb-6 w-100%">
      <NuxtLink class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer" @click="state = LoginState">
        {{ t('pages.register.back') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.register.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
