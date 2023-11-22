<script lang="ts" setup>
import { object, string, ref as yref } from 'yup'
import { UPDATE_PASSWORD_FORM } from '~/utils/forms'

const { t, m } = useI18n()
const updatePassword = useUpdatePassword()

const oldPasswordField = ref<Nullable<CompElement>>(null)
const newPasswordField = ref<Nullable<CompElement>>(null)
const confirmNewPasswordField = ref<Nullable<CompElement>>(null)

const schema = object({
  oldPassword: string()
    .required(t('forms.update_password.errors.missing_old_password')),
  newPassword: string()
    .required(t('forms.update_password.errors.missing_new_password'))
    .min(UPDATE_PASSWORD_FORM.password.min, t('forms.update_password.errors.password_too_short', { min: UPDATE_PASSWORD_FORM.password.min }))
    .max(UPDATE_PASSWORD_FORM.password.max, t('forms.update_password.errors.password_too_long', { max: UPDATE_PASSWORD_FORM.password.max })),
  confirmNewPassword: string()
    .required(t('forms.update_password.errors.missing_new_password'))
    .oneOf([yref('newPassword')], t('forms.update_password.errors.passwords_mismatch')),
})
const { handleSubmit, resetForm, setFieldError } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit((values) => {
  updatePassword(values.oldPassword, values.newPassword)
    .then(() => resetForm())
    .catch((error) => {
      for (const field in error.response._data.fields) {
        setFieldError(field, m(error.response._data.fields[field]))
      }
    })
})
</script>

<template>
  <form v-focus-trap @submit="onSubmit">
    <div class="mb-3">
      <FormInputText
        id="oldPassword"
        ref="oldPasswordField"
        class="w-full"
        name="oldPassword"
        type="password"
        :label="t('forms.update_password.fields.old_password')"
      />
    </div>
    <div class="mb-3">
      <FormInputText
        id="newPassword"
        ref="newPasswordField"
        class="w-full"
        name="newPassword"
        type="password"
        :label="t('forms.update_password.fields.new_password')"
      />
    </div>
    <div class="mb-3">
      <FormInputText
        id="confirmNewPassword"
        ref="confirmNewPasswordField"
        class="w-full"
        name="confirmNewPassword"
        type="password"
        :label="t('forms.update_password.fields.confirm_new_password')"
      />
    </div>
    <div class="flex flex-row-reverse items-center gap-3 pt-3">
      <PButton :label="t('forms.update_password.submit')" type="submit" />
    </div>
  </form>
</template>
