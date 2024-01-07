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
  default: {
    content: null,
    severity: undefined,
  },
})

const registrationEnabled = ref(false)
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

const onSubmit = handleSubmit((values) => {
  loading.value = true
  login(values).catch((error) => {
    loading.value = false
    const status = error?.response?.status

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

    message.value.content = m(error.data.title)
    message.value.severity = 'error'
  })
})

function hideErrorMessage() {
  message.value.content = null
}

onMounted(() => {
  $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration/public')
    .then(config => registrationEnabled.value = config.enableRegistrations)
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage
      v-show="message.content"
      :pt="{ root: { class: 'important-mt-0' } }"
      :severity="message.severity"
      :icon="{
        success: 'i-tabler-circle-check-filled',
        info: 'i-tabler-info-circle-filled',
        warn: 'i-tabler-alert-circle-filled',
        error: 'i-tabler-alert-circle-filled',
      }[message.severity!]"
      :closable="false"
    >
      {{ message.content }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="login_username"
        ref="usernameField"
        class="w-full"
        name="username"
        type="text"
        :label="t('forms.login.fields.username')"
        :placeholder="usernamePlaceholder"
        :transform-value="usernameRestrict"
        start-icon="i-tabler-user"
        autocomplete="username"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="login_password"
        ref="passwordField"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.login.fields.password')"
        placeholder="••••••••••••••••"
        start-icon="i-tabler-lock"
        autocomplete="current-password"
        @input="hideErrorMessage"
      />
    </div>

    <div class="flex flex-row-reverse items-center justify-between mb-6 px-2 w-100%">
      <NuxtLink to="/reset-password" class="font-medium no-underline text-blue-500 text-right cursor-pointer">
        {{ t('pages.login.forgot_password') }}
      </NuxtLink>
      <NuxtLink v-show="registrationEnabled" to="/register" class="font-medium no-underline text-blue-500 text-right cursor-pointer">
        {{ t('pages.login.register') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
