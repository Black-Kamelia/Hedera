<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'
import { getRandomDeveloperName } from '~/utils/developerNames'

const { t } = useI18n()

definePageMeta({
  layout: 'centercard',
})
useHead({
  title: t('pages.login.tab_title'),
})

const schema = object({
  username: string().required(t('pages.login.form.errors.missing_username')),
  password: string().required(t('pages.login.form.errors.missing_password')),
})
const { handleSubmit, errors } = useForm({
  validationSchema: schema,
})

const { tokens, setTokens } = useAuth()
onMounted(() => {
  if (tokens)
    navigateTo('/')
})

const { execute } = useAPI('/login', { method: 'POST' }, { immediate: false })
const onSubmit = handleSubmit(async (values) => {
  const { data, error } = await execute({ data: values })
  if (!error.value) {
    setTokens(data.value)
    navigateTo('/')
  }
  else {
    console.error('Login failed')
  }
})
</script>

<template>
  <div class="text-center mb-5">
    <h1 class="font-extrabold text-4xl mb-1">
      Hedera
    </h1>
    <h2 class="font-extrabold text-2xl mb-3">
      {{ t('pages.login.title') }}
    </h2>
  </div>

  <form @submit="onSubmit">
    <label for="email1" class="block font-900 font-medium mb-2">{{ t('pages.login.form.fields.username') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left">
        <i class="i-tabler-user" />
        <InputText name="username" type="text" :placeholder="getRandomDeveloperName()" class="w-full" />
      </span>
      <small v-if="errors.username" id="text-error" class="p-error mt-1">{{ errors.username }}</small>
    </div>

    <label for="password1" class="block font-900 font-medium mb-2">{{ t('pages.login.form.fields.password') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left">
        <i class="i-tabler-lock" />
        <InputText name="password" type="password" placeholder="••••••••••••••••" class="w-full" />
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

    <PButton :label="t('pages.login.form.submit')" class="w-full" type="submit" />
  </form>
</template>
