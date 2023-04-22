<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'
import { getRandomDeveloperName } from '~/utils/developerNames'

const { t } = useI18n()

definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
useHead({
  title: t('pages.login.tab_title'),
})

const schema = object({
  email: string().email().required(t('pages.resetPassword.form.errors.missing_email')),
})
const { handleSubmit, errors, resetField } = useForm({
  validationSchema: schema,
})

const { tokens, login } = useAuth()
onMounted(() => {
  if (tokens.value)
    navigateTo('/files', { replace: true })
})

const usernamePlaceholder = getRandomDeveloperName()
const showErrorMessage = ref(false)

function hideErrorMessage() {
  showErrorMessage.value = false
}

const onSubmit = handleSubmit((values) => {
  login(values).catch((err) => {
    if (err.response?.status === 401) {
      resetField('password')
      showErrorMessage.value = true
    }
  })
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

  <PMessage v-show="showErrorMessage" severity="error" icon="i-tabler-alert-circle-filled" :closable="false">
    {{ t('pages.login.errors.invalid_credentials') }}
  </PMessage>

  <PMessage v-show="false" severity="success" icon="i-tabler-circle-check-filled" :closable="false">
    {{ t('pages.resetPassword.messages.link_sent') }}
  </PMessage>

  <form @submit="onSubmit">
    <label for="email1" class="block font-900 font-medium mb-2">{{ t('pages.resetPassword.form.fields.email') }}</label>
    <div class="mb-6">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-mail" />
        <InputText
          name="email" type="text" :placeholder="`${usernamePlaceholder}@example.com`" class="w-full"
          @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.email" id="text-error" class="p-error mt-1">{{ errors.email }}</small>
    </div>

    <PButton :label="t('pages.resetPassword.form.submit')" class="w-full" type="submit" />
  </form>
</template>
