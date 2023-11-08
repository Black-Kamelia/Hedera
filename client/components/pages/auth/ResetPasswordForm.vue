<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const { t } = useI18n()

const usernamePlaceholder = getRandomDeveloperUsername()
const loading = ref(false)

const schema = object({
  email: string()
    .required(t('forms.register.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),

})
const { handleSubmit } = useForm({ validationSchema: schema })

const onSubmit = handleSubmit(async () => {})
</script>

<template>
  <form @submit="onSubmit">
    <div class="mb-3">
      <FormInputText
        id="resetPwd_email"
        class="w-full"
        name="email"
        type="email"
        :label="t('forms.register.fields.email')"
        :placeholder="`${usernamePlaceholder}@example.com`"
        start-icon="i-tabler-mail"
        autocomplete="email"
      />
    </div>

    <div class="flex flex-row items-center justify-end mb-6 w-100%">
      <NuxtLink to="/login" class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">
        {{ t('pages.register.back') }}
      </NuxtLink>
    </div>

    <PButton :label="t('forms.reset_password.submit')" class="w-full" type="submit" :loading="loading" />
  </form>
</template>
