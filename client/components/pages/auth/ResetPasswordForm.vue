<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

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
    .required(t('forms.register.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),

})
const { handleSubmit, setFieldError, resetForm } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  $fetchAPI<any>('/reset-password', { method: 'POST', body: values })
    .then((response) => {
      resetForm()
      message.value.content = t(response.title.key)
      message.value.severity = 'success'
    })
    .catch((err) => {
      if (err.response) {
        if (err.response._data.fields) {
          setFieldErrors(err.response._data.fields, setFieldError)
        } else {
          message.value.content = t(err.response._data.title.key)
          message.value.severity = 'error'
        }
      }
    })
    .finally(() => {
      loading.value = false
    })
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
        :label="t('forms.register.fields.email')"
        :placeholder="`${usernamePlaceholder}@example.com`"
        start-icon="i-tabler-mail"
        autocomplete="email"
        @input="hideErrorMessage()"
      />
    </div>

    <div class="flex flex-row items-center justify-end mb-6 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.register.back') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.reset_password.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
