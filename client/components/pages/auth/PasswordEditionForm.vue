<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import { UPDATE_PASSWORD_FORM } from '~/utils/forms'
import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()
const { logout } = useAuth()
const updatePassword = useUpdatePassword()
const { setUser } = useAuth()
const dialog = useConfirmEvent()

const loading = ref(false)
const passwordField = ref<Nullable<CompElement>>(null)

const message = reactive<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
})

onMounted(() => passwordField.value?.$el.focus({ preventScroll: true }))

const schema = object({
  password: string()
    .required(t('forms.change_password.errors.missing_password'))
    .min(UPDATE_PASSWORD_FORM.password.min, t('forms.change_password.errors.password_too_short', { min: UPDATE_PASSWORD_FORM.password.min }))
    .max(UPDATE_PASSWORD_FORM.password.max, t('forms.change_password.errors.password_too_long', { max: UPDATE_PASSWORD_FORM.password.max })),
  confirmPassword: string()
    .required(t('forms.change_password.errors.missing_password_confirmation'))
    .oneOf([yref('password')], t('forms.change_password.errors.passwords_mismatch')),
})
const { handleSubmit, resetField } = useForm({
  validationSchema: schema,
})

function hideErrorMessage() {
  message.content = null
}

const onSubmit = handleSubmit((values) => {
  loading.value = true
  updatePassword(null, values.password, true)
    .then(() => {
      setUser({ forceChangePassword: false })
      useEventBus(ForcePasswordChangeDoneEvent).emit()
    })
    .catch((error) => {
      const status = error?.response?.status
      const errorKey = error?.response?._data?.title?.key
      if (status === 500) {
        resetField('password')
        resetField('confirmPassword')
        passwordField.value?.$el.focus()
        message.content = t('forms.change_password.errors.server_error')
        message.severity = 'error'
      } else if (status === 403 && errorKey === 'errors.users.force_change_password_conflict') {
        dialog.require({
          header: 'Password change conflict',
          message: 'It appears that you have already changed your password somewhere else. You will need to reconnect.',
          rejectClass: 'hidden',
          acceptLabel: 'Back to login',
          acceptIcon: 'i-tabler-arrow-left',
          accept: () => logout(),
        })
      }
      loading.value = false
    })
})
</script>

<template>
  <form @submit="onSubmit">
    <Transition name="fade" mode="out-in">
      <PMessage v-if="!message.content" :pt="{ root: { class: 'important-mt-0' } }" severity="info" icon="i-tabler-shield-lock-filled" :closable="false">
        {{ t('pages.change_password.message') }}
      </PMessage>
      <PMessage v-else :pt="{ root: { class: 'important-mt-0' } }" :severity="message.severity" icon="i-tabler-alert-circle-filled" :closable="false">
        {{ message.content }}
      </PMessage>
    </Transition>

    <div class="mb-3">
      <FormInputText
        id="updatePwd_password"
        ref="passwordField"
        class="w-full"
        name="password"
        type="password"
        :label="t('forms.change_password.fields.password')"
        placeholder="••••••••••••••••"
        autocomplete="new-password"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mb-3">
      <FormInputText
        id="updatePwd_confirmPassword"
        class="w-full"
        name="confirmPassword"
        type="password"
        :label="t('forms.change_password.fields.confirm_password')"
        placeholder="••••••••••••••••"
        autocomplete="new-password"
        @input="hideErrorMessage"
      />
    </div>

    <div class="mt-6 flex flex-col-reverse sm:flex-row gap-3">
      <PButton :label="t('global.logout')" class="w-full" :disabled="loading" outlined @click="logout()" />
      <PButton :label="t('forms.change_password.submit')" class="w-full" type="submit" :loading="loading" />
    </div>

    <ConfirmDialog
      :pt="{
        closeButton: { class: 'hidden' },
      }"
    />
  </form>
</template>

<style scoped>
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
