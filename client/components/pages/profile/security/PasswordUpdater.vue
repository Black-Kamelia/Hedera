<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'

const { t } = useI18n()
const updatePassword = useUpdatePassword()

const oldPasswordField = ref<Nullable<CompElement>>(null)
const newPasswordField = ref<Nullable<CompElement>>(null)
const confirmNewPasswordField = ref<Nullable<CompElement>>(null)

const schema = object({
  oldPassword: string()
    .required(t('forms.login.errors.missing_password')),
  newPassword: string()
    .required(t('forms.login.errors.missing_password'))
    .min(8, t('forms.login.errors.password_too_short'))
    .max(64, t('forms.login.errors.password_too_long')),
  confirmNewPassword: string()
    .required(t('forms.login.errors.missing_password'))
    .oneOf([yref('newPassword')], t('forms.login.errors.passwords_mismatch')),
})
const { handleSubmit, resetForm } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit((values) => {
  updatePassword(values.oldPassword, values.newPassword)
    .then(() => resetForm())
})
</script>

<template>
  <div class="p-card p-7">
    <h2 class="text-lg font-bold mb-5">
      Modifier le mot de passe
    </h2>
    <form v-focus-trap @submit="onSubmit">
      <FormInputText
        id="password"
        ref="oldPasswordField"
        class="w-full"
        name="oldPassword"
        type="password"
        :label="t('forms.update_password.fields.old_password')"
      />
      <FormInputText
        id="password"
        ref="newPasswordField"
        class="w-full"
        name="newPassword"
        type="password"
        :label="t('forms.update_password.fields.new_password')"
      />
      <FormInputText
        id="password"
        ref="confirmNewPasswordField"
        class="w-full"
        name="confirmNewPassword"
        type="password"
        :label="t('forms.update_password.fields.confirm_new_password')"
      />
      <div class="flex flex-row-reverse items-center gap-3 pt-3">
        <PButton :label="t('forms.update_password.submit')" type="submit" />
        <PButton :label="t('pages.login.forgot_password')" text />
      </div>
    </form>
  </div>
</template>
