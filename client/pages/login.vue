<script setup lang="ts">
import { useForm } from 'vee-validate'
import { object, string } from 'yup'

const { t, m } = useI18n()
const { login } = useAuth()

const usernamePlaceholder = getRandomDeveloperUsername()
const message = reactive<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
})

const loading = ref(false)
const usernameField = ref<Nullable<CompElement>>(null)
const passwordField = ref<Nullable<CompElement>>(null)

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
const { currentRoute } = useRouter()

onMounted(() => {
  const query = currentRoute.value.query
  const params = Object.keys(query)
  if (params.includes('reason')) {
    message.content = t(`pages.login.reasons.${query.reason}`)
    message.severity = 'warn'
  }
})

const schema = object({
  username: string()
    .required(t('forms.login.errors.missing_username'))
    .matches(/^[a-z0-9_\-.]+$/, t('forms.login.errors.invalid_username')),
  password: string()
    .required(t('forms.login.errors.missing_password')),
})
const { handleSubmit, resetField } = useForm({
  validationSchema: schema,
})

function hideErrorMessage() {
  message.content = null
}

useEventBus(LoggedInEvent).on((event) => {
  if (event.error) {
    const status = event.error?.response?.status

    if (status === 500) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
      message.content = t('forms.login.errors.server_error')
      message.severity = 'error'
      return
    }

    if (status === 401) {
      resetField('password')
      passwordField.value?.$el.focus()
    }
    if (status === 403) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
    }

    message.content = m(event.error.data.title)
    message.severity = 'error'
  } else {
    const redirect = useRoute().query.redirect?.toString()
    const to = redirect && (redirect.startsWith('%2F') || redirect.startsWith('/'))
      ? decodeURIComponent(redirect)
      : '/files'

    try {
      navigateTo(to, { replace: true })
    } catch {
      navigateTo('/files', { replace: true })
    }
  }
})

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  await login(values)
  loading.value = false
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <h2 class="font-600 text-3xl mb-3">
      {{ t('pages.login.title') }}
    </h2>
  </div>

  <PMessage v-show="message.content" :severity="message.severity" icon="i-tabler-alert-circle-filled" :closable="false">
    {{ message.content }}
  </PMessage>

  <form v-focus-trap @submit="onSubmit">
    <div class="mb-3">
      <FormInputText
        id="username"
        ref="usernameField"
        class="w-full"
        name="username"
        type="text"
        :label="t('forms.login.fields.username')"
        :placeholder="usernamePlaceholder"
        :transform-value="usernameRestrict"
        start-icon="i-tabler-user"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="password"
        ref="passwordField"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.login.fields.password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        @input="hideErrorMessage"
      />
    </div>

    <div class="flex flex-row-reverse items-center justify-between mb-6 w-100%">
      <NuxtLink to="/reset-password" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.login.forgot_password') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
