<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const emit = defineEmits<{
  (event: 'completed'): void
}>()

const { t } = useI18n()
const setFieldErrors = useFeedbackFormErrors()

const usernamePlaceholder = getRandomDeveloperUsername()
const loading = ref(false)
const message = ref<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
})

const schema = object({
  email: string()
    .required(t('forms.reset_password_request.errors.missing_email'))
    .email(t('forms.reset_password_request.errors.invalid_email')),

})
const { handleSubmit, setFieldError } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  $fetchAPI<void>('/request-reset-password', { method: 'POST', body: values })
    .then(() => emit('completed'))
    .catch((err) => {
      if (err.response && err.response._data.fields) {
        setFieldErrors(err.response._data.fields, setFieldError)
      }
    })
    .finally(() => loading.value = false)
})

function hideErrorMessage() {
  message.value.content = null
}
</script>

<template>
  <form @submit="onSubmit">
    <PMessage
      v-show="(message.content && message.severity)"
      :pt="{ root: { class: 'important-mt-0' } }"
      :severity="message.severity"
      :icon="{
        success: 'i-tabler-circle-check-filled',
        info: 'i-tabler-info-circle-filled',
        warn: 'i-tabler-alert-triangle-filled',
        error: 'i-tabler-alert-circle-filled',
      }[message.severity!]" :closable="false"
    >
      {{ message.content }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="resetPwd_email"
        class="w-full"
        name="email"
        type="email"
        :label="t('forms.reset_password_request.fields.email')"
        :placeholder="`${usernamePlaceholder}@example.com`"
        start-icon="i-tabler-mail"
        autocomplete="email"
        @input="hideErrorMessage()"
      />
    </div>

    <div class="flex flex-row items-center justify-between mb-6 px-2 w-100%">
      <NuxtLink class="font-medium no-underline text-blue-500 text-right cursor-pointer" @click="emit('completed')">
        {{ t('pages.reset_password.input_token') }}
      </NuxtLink>
      <NuxtLink to="/login" class="font-medium no-underline text-blue-500 text-right cursor-pointer">
        {{ t('pages.reset_password.login') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.reset_password_request.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
