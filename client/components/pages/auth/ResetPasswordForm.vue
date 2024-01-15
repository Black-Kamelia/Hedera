<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import { DateTime } from 'luxon'
import { UPDATE_PASSWORD_FORM } from '~/utils/forms'

const { token, tokenMetadata } = defineProps<{
  token: string
  tokenMetadata: ResetPasswordTokenDTO
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()

const loading = ref(false)
const schema = object({
  password: string()
    .required(t('forms.reset_password.errors.missing_password'))
    .min(UPDATE_PASSWORD_FORM.password.min, t('forms.reset_password.errors.password_too_short', { min: UPDATE_PASSWORD_FORM.password.min }))
    .max(UPDATE_PASSWORD_FORM.password.max, t('forms.reset_password.errors.password_too_long', { max: UPDATE_PASSWORD_FORM.password.max })),
  passwordConfirmation: string()
    .required(t('forms.reset_password.errors.missing_password_confirmation'))
    .oneOf([yref('password')], t('forms.reset_password.errors.passwords_mismatch')),
})
const { handleSubmit, setFieldError } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit((values) => {
  loading.value = true
  values.token = token
  $fetchAPI<void>('/reset-password', { method: 'POST', body: values })
    .then(() => {
      navigateTo({ path: '/login', query: { reason: 'password_reset' } })
    })
    .catch((err) => {
      if (err.response?._data.fields) {
        setFieldErrors(err.response._data.fields, setFieldError)
      }
    })
    .finally(() => loading.value = false)
})

const expiration = computed(() => {
  return DateTime.fromISO(tokenMetadata.expiration).plus({ minute: 1 }).toRelative({ unit: 'minutes' })
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage
      :pt="{ root: { class: 'important-mt-0' } }"
      severity="info"
      icon="i-tabler-info-circle-filled"
      :closable="false"
    >
      {{ t('forms.reset_password.message', { expiration }) }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="resetPwd_newPassword"
        class="w-full"
        name="password"
        type="password"
        autocomplete="new-password"
        :label="t('forms.reset_password.fields.password')"
        placeholder="••••••••••••••••"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="resetPwd_confirmNewPassword"
        class="w-full"
        name="passwordConfirmation"
        type="password"
        autocomplete="new-password"
        :label="t('forms.reset_password.fields.password_confirmation')"
        placeholder="••••••••••••••••"
      />
    </div>

    <div class="flex flex-row items-center justify-end mb-6 px-2 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline text-blue-500 text-right cursor-pointer">
        {{ t('pages.register.back') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.reset_password.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
