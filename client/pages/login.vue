<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'
import { getRandomDeveloperName } from '~/utils/developerNames'

const { t, e } = useI18n()

definePageMeta({
  layout: 'centercard',
})
useHead({
  title: t('pages.login.tab_title'),
})

const schema = object({
  username: string().required(t('forms.login.errors.missing_username')),
  password: string().required(t('forms.login.errors.missing_password')),
})
const { handleSubmit, errors, resetField } = useForm({
  validationSchema: schema,
})

const { tokens, login } = useAuth()
onMounted(() => {
  if (tokens.value)
    navigateTo('/', { replace: true })
})

const usernamePlaceholder = getRandomDeveloperName()
const errorMessage = ref<Nullable<String>>(null)

function hideErrorMessage() {
  errorMessage.value = null
}

useEventBus(LoggedInEvent).on((event) => {
  if (event.error.response?.status === 401) {
    resetField('password')
    errorMessage.value = e(event.error.response?.data ?? { key: 'errors.unknown' })
  }
})

const onSubmit = handleSubmit(login)
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-extrabold text-5xl mb-1">
      Hedera
    </h1>
    <h2 class="font-extrabold text-3xl mb-3">
      {{ t('pages.login.title') }}
    </h2>
  </div>

  <PMessage v-show="errorMessage" severity="error" icon="i-tabler-alert-circle-filled" :closable="false">
    {{ errorMessage }}
  </PMessage>

  <form @submit="onSubmit">
    <label for="email1" class="block font-900 font-medium mb-2">{{ t('forms.login.fields.username') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-user" />
        <InputText
          name="username" type="text" :placeholder="usernamePlaceholder" class="w-full" @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.username" id="text-error" class="p-error mt-1">{{ errors.username }}</small>
    </div>

    <label for="password1" class="block font-900 font-medium mb-2">{{ t('forms.login.fields.password') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-lock" />
        <InputText
          name="password" type="password" placeholder="••••••••••••••••" class="w-full" @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.password" id="text-error" class="p-error mt-1">{{ errors.password }}</small>
    </div>

    <div class="flex flex-row items-center justify-between mb-6 w-100%">
      <div />
      <!--
      <div v-tooltip.bottom="{ value: 'Coming soon', class: 'p-0' }" class="flex flex-row items-center">
        <Checkbox name="rememberMe" :disabled="true" :label="t('pages.login.form.fields.remember_me')" />
      </div>
      -->
      <a class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.login.forgot_password') }}
      </a>
    </div>

    <PButton :label="t('forms.login.submit')" class="w-full" type="submit" />
  </form>
</template>
