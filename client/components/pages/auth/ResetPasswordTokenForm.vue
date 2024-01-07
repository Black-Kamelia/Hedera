<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const { token } = defineProps<{
  token: string
}>()
const emit = defineEmits<{
  (event: 'completed', token: string, dto: ResetPasswordTokenDTO): void
  (event: 'back'): void
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()

const loading = ref(false)
const schema = object({
  token: string()
    .required(t('forms.reset_password_token.errors.missing_token'))
    .matches(/^[0-9a-f]{32}$/, t('forms.reset_password_token.errors.invalid_token')),
})
const { handleSubmit, setFieldError, setFieldValue, resetForm } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  $fetchAPI<ResetPasswordTokenDTO>('/check-reset-password-token', { method: 'POST', body: values })
    .then(response => emit('completed', values.token, response))
    .catch((err) => {
      if (err.response && err.response._data.fields) {
        setFieldErrors(err.response._data.fields, setFieldError)
      }
    })
    .finally(() => loading.value = false)
})

function back() {
  emit('back')
  resetForm()
  navigateTo({ path: '/reset-password' }, { replace: true })
}

onMounted(() => {
  if (token) {
    setFieldValue('token', token, true)
    onSubmit()
  }
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage
      :pt="{ root: { class: 'important-mt-0' } }"
      severity="info"
      icon="i-tabler-shield-lock-filled"
      :closable="false"
    >
      {{ t('forms.reset_password_token.message') }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="resetPwd_token"
        class="w-full"
        name="token"
        type="text"
        :label="t('forms.reset_password_token.fields.token')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-key"
        autocomplete="off"
        :disabled="loading"
      />
    </div>

    <div class="flex flex-row items-center justify-between mb-6 px-2 w-100%">
      <NuxtLink class="font-medium no-underline text-blue-500 text-right cursor-pointer" @click="back()">
        {{ t('pages.reset_password.back') }}
      </NuxtLink>
      <NuxtLink to="/login" class="font-medium no-underline text-blue-500 text-right cursor-pointer">
        {{ t('pages.reset_password.login') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.reset_password_token.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
