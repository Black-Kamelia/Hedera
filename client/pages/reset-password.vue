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

const onSubmit = handleSubmit((values) => {
  console.log('Not implemented yet')
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-extrabold text-4xl mb-1">
      Hedera
    </h1>
    <h2 class="font-extrabold text-2xl mb-3">
      {{ t('pages.resetPassword.title') }}
    </h2>
  </div>

  <form @submit="onSubmit">
    <label for="email1" class="block font-900 font-medium mb-2">{{ t('forms.resetPassword.fields.email') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-mail" />
        <InputText
          name="email" type="text" :placeholder="`${usernamePlaceholder}@example.com`" class="w-full"
          @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.email" id="text-error" class="p-error mt-1">{{ errors.email }}</small>
    </div>

    <div class="flex flex-row-reverse items-center justify-between mb-6 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.resetPassword.login') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.submit')" class="w-full" type="submit" />
  </form>
</template>
