<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import { UpdatePasswordForm } from '~/utils/forms'
import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()
const { logout } = useAuth()
const updatePassword = useUpdatePassword()
const { setUser } = useAuth()

const loading = ref(false)
const passwordField = ref<Nullable<CompElement>>(null)

onMounted(() => passwordField.value?.$el.focus({ preventScroll: true }))

const schema = object({
  password: string()
    .required(t('forms.change_password.errors.missing_password'))
    .min(UpdatePasswordForm.password.min, t('forms.change_password.errors.password_too_short', { min: UpdatePasswordForm.password.min }))
    .max(UpdatePasswordForm.password.max, t('forms.change_password.errors.password_too_long', { max: UpdatePasswordForm.password.max })),
  confirmPassword: string()
    .required(t('forms.change_password.errors.missing_password_confirmation'))
    .oneOf([yref('password')], t('forms.change_password.errors.passwords_mismatch')),
})
const { handleSubmit } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit(async (values) => {
  loading.value = true
  updatePassword(null, values.password)
    .then(() => {
      setUser({ forceChangePassword: false })
      useEventBus(ForcePasswordChangeDoneEvent).emit()
    })
    .catch(() => loading.value = false)
})
</script>

<template>
  <form @submit="onSubmit">
    <PMessage :pt="{ root: { class: 'important-mt-0' } }" severity="info" icon="i-tabler-shield-lock-filled" :closable="false">
      {{ t('pages.change_password.message') }}
    </PMessage>

    <div class="mb-3">
      <FormInputText
        id="password"
        ref="passwordField"
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

    <div class="mt-6 flex flex-col-reverse sm:flex-row gap-3">
      <PButton :label="t('global.logout')" class="w-full" :disabled="loading" outlined @click="logout(true)" />
      <PButton :label="t('forms.change_password.submit')" class="w-full" type="submit" :loading="loading" />
    </div>
  </form>
</template>
