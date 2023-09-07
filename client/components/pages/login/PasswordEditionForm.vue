<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const { t } = useI18n()
const { logout } = useAuth()

const loading = ref(false)

const schema = object({
  password: string()
    .required(t('forms.change_password.errors.missing_password')),
  confirmPassword: string()
    .required(t('forms.change_password.errors.missing_password_confirmation')),
})
const { handleSubmit } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  // await login(values)
  loading.value = false
})
</script>

<template>
  <form v-focus-trap @submit="onSubmit">
    <PMessage severity="info" icon="i-tabler-shield-lock-filled" :closable="false">
      You must change your password before continuing
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="password"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.change_password.fields.password')"
        placeholder="••••••••••••••••"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="confirmPassword"
        class="w-full"
        name="confirmPassword"
        type="password"
        :label="t('forms.change_password.fields.confirm_password')"
        placeholder="••••••••••••••••"
      />
    </div>

    <div class="flex flex-row gap-3">
      <PButton :label="t('global.logout')" class="mt-6 w-full" :loading="loading" outlined @click="logout(true)" />
      <PButton :label="t('forms.change_password.submit')" class="mt-6 w-full" type="submit" :loading="loading" />
    </div>
  </form>
</template>
