<script setup lang="ts">
const { t } = useI18n()

usePageName(() => t('pages.reset_password.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
/*
import { object, string } from 'yup'

interface ResetPasswordForm {
  email: string
}

const { t } = useI18n()

usePageName(() => t('pages.reset_password.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth', 'card-transitions'],
})

const schema = object({
  email: string()
    .email(t('forms.reset_password.errors.invalid_email'))
    .required(t('forms.reset_password.errors.missing_email')),
})
const { handleSubmit } = useForm<ResetPasswordForm>({
  validationSchema: schema,
})

const usernamePlaceholder = getRandomDeveloperUsername()
const showErrorMessage = ref(false)

function hideErrorMessage() {
  showErrorMessage.value = false
}

const onSubmit = handleSubmit((_) => {
  // TODO: not implemented yet
})
 */
</script>

<template>
  <AuthContainer :initial-state="ResetPasswordState" />
</template>
