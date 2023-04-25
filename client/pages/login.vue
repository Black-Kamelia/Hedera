<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'
import { getRandomDeveloperName } from '~/utils/developerNames'
import PInputText from "primevue/inputtext";

const { t, e } = useI18n()
const { login } = useAuth()

const usernamePlaceholder = getRandomDeveloperName()
const message = ref<Nullable<string>>(null)
const messageSeverity = ref<'success' | 'info' | 'warn' | 'error' | undefined>('error')

const usernameField = ref()
const passwordField = ref()

usePageName(t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
const { currentRoute } = useRouter()

onMounted(() => {
  if (Object.keys(currentRoute.value.query).includes('expired')) {
    message.value = t('errors.tokens.expired_or_invalid')
    messageSeverity.value = 'warn'
  }
})

const schema = object({
  username: string().required(t('forms.login.errors.missing_username')),
  password: string().required(t('forms.login.errors.missing_password')),
})
const { handleSubmit, errors, resetField } = useForm({
  validationSchema: schema,
})

function hideErrorMessage() {
  message.value = null
}

useEventBus(LoggedInEvent).on((event) => {
  if (event.error) {
    if (event.error?.response?.status === 401) {
      resetField('password')
      passwordField.value?.$el.focus()
    }
    if (event.error?.response?.status === 403) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
    }

    message.value = e(event.error)
    messageSeverity.value = 'error'
  }
  else {
    navigateTo('/files')
  }
})

const onSubmit = handleSubmit(login)
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-extrabold text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <h2 class="font-extrabold text-3xl mb-3">
      {{ t('pages.login.title') }}
    </h2>
  </div>

  <PMessage v-show="message" :severity="messageSeverity" icon="i-tabler-alert-circle-filled" :closable="false">
    {{ message }}
  </PMessage>

  <form @submit="onSubmit" v-focus-trap>
    <label for="username" class="block font-900 font-medium mb-2">{{ t('forms.login.fields.username') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-user" />
        <InputText
          ref="usernameField" id="username" name="username" type="text"
          :placeholder="usernamePlaceholder" class="w-full" @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.username" id="text-error" class="p-error mt-1">{{ errors.username }}</small>
    </div>

    <label for="password" class="block font-900 font-medium mb-2">{{ t('forms.login.fields.password') }}</label>
    <div class="mb-3">
      <span class="p-input-icon-left w-full">
        <i class="i-tabler-lock" />
        <InputText
          ref="passwordField" id="password" name="password" type="password"
          placeholder="••••••••••••••••" class="w-full" @input="hideErrorMessage"
        />
      </span>
      <small v-if="errors.password" id="text-error" class="p-error mt-1">{{ errors.password }}</small>
    </div>

    <div class="flex flex-row-reverse items-center justify-between mb-6 w-100%">
      <NuxtLink to="/reset-password" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.login.forgot_password') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.submit')" class="w-full" type="submit" />
  </form>
</template>
