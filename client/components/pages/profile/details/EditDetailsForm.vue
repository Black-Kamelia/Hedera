<script lang="ts" setup>
import { object, string } from 'yup'

const { t } = useI18n()
const updateDetails = useUpdateDetails()
const setFieldErrors = useFeedbackFormErrors()
const { user } = useAuth()

const loading = ref(false)

const schema = object({
  username: string()
    .required(t('forms.update_details.errors.missing_username')),
  email: string()
    .required(t('forms.update_details.errors.missing_email'))
    .email(t('forms.update_details.errors.invalid_email')),
})
const { handleSubmit, setFieldError, setFieldValue } = useForm({
  validationSchema: schema,
})

const onSubmit = handleSubmit((values) => {
  loading.value = true
  updateDetails(values)
    .catch(error => setFieldErrors(error.response._data.fields, setFieldError))
    .finally(() => loading.value = false)
})

onMounted(() => {
  setFieldValue('username', user?.username)
  setFieldValue('email', user?.email)
})
</script>

<template>
  <form @submit="onSubmit">
    <div class="flex flex-row gap-4">
      <div class="mb-3 w-full">
        <FormInputText
          id="updateDetails_username"
          class="w-full"
          name="username"
          type="text"
          :label="t('forms.update_details.fields.username')"
        />
      </div>
      <div class="mb-3 w-full">
        <FormInputText
          id="updateDetails_email"
          class="w-full"
          name="email"
          type="email"
          :label="t('forms.update_details.fields.email')"
        />
      </div>
    </div>
    <div class="flex flex-row-reverse items-center gap-3 pt-3">
      <PButton :label="t('forms.update_details.submit')" type="submit" :loading="loading" />
    </div>
  </form>
</template>
