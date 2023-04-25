<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'
import { getRandomDeveloperName } from '~/utils/developerNames'

const { t } = useI18n()

usePageName(t('pages.resetPassword.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})

const schema = object({
  email: string()
    .email(t('forms.resetPassword.errors.invalid_email'))
    .required(t('forms.resetPassword.errors.missing_email')),
})
const { handleSubmit, errors } = useForm({
  validationSchema: schema,
})

const usernamePlaceholder = getRandomDeveloperName()
const showErrorMessage = ref(false)

function hideErrorMessage() {
  showErrorMessage.value = false
}

const onSubmit = handleSubmit((_) => {
  // TODO: not implemented yet
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <h2 class="font-600 text-3xl mb-3">
      {{ t('pages.resetPassword.title') }}
    </h2>
  </div>

  <form @submit="onSubmit">
    <InputText
      id="email"
      class="w-full"
      name="email"
      type="email"
      :label="t('forms.resetPassword.fields.email')"
      :placeholder="`${usernamePlaceholder}@example.com`"
      start-icon="i-tabler-mail"
      @input="hideErrorMessage"
    />

    <div class="flex flex-row-reverse items-center justify-between mb-6 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.resetPassword.login') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.submit')" class="w-full" type="submit" />
  </form>
</template>
