<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const { t, m } = useI18n()
const { login } = useAuth()

const usernamePlaceholder = getRandomDeveloperUsername()
const message = defineModel<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>('message', {
  default: reactive({
    content: null,
    severity: undefined,
  }),
})

const loading = ref(false)
const usernameField = ref<Nullable<CompElement>>(null)
const passwordField = ref<Nullable<CompElement>>(null)

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

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  login(values).catch(() => loading.value = false)
})

function hideErrorMessage() {
  message.value.content = null
}

useEventBus(LoggedInEvent).on((event) => {
  if (event.error) {
    const status = event.error?.response?.status

    if (status === 500) {
      resetField('username')
      resetField('password')
      usernameField.value?.$el.focus()
      message.value.content = t('forms.login.errors.server_error')
      message.value.severity = 'error'
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

    message.value.content = m(event.error.data.title)
    message.value.severity = 'error'
  }
})
</script>

<template>
  <form v-focus-trap @submit="onSubmit">
    <PMessage v-show="message.content" :severity="message.severity" icon="i-tabler-alert-circle-filled" :closable="false">
      {{ message.content }}
    </PMessage>

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

    <PButton tabindex="10" :label="t('forms.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
