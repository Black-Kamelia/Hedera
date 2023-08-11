<script lang="ts" setup>
import { boolean, object, string, ref as yref } from 'yup'
import { useForm } from 'vee-validate'
import FormDropdown from '~/components/input/FormDropdown.vue'

const emit = defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const dev = getRandomDeveloperUsername()
const { user } = useAuth()
// const createUser = useCreateUser()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)
const newToken = ref<Nullable<UserRepresentationDTO>>(null)

const roles = [
  { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
  { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
]
function roleDisabled(role: string) {
  if (user?.role === 'OWNER') return false
  if (user?.role === 'ADMIN' && role === 'ADMIN') return true
  return true
}

const schema = object({
  // name: string()
  //  .required(t('pages.profile.tokens.create_dialog.errors.missing_name')),
  username: string()
    .required(t('forms.create_user.errors.missing_username')),
  password: string()
    .required(t('forms.create_user.errors.missing_password'))
    .min(8, t('forms.create_user.errors.password_too_short', { min: 8 }))
    .max(128, t('forms.create_user.errors.password_too_long', { max: 128 })),
  confirmPassword: string()
    .required(t('forms.create_user.errors.missing_confirm_password'))
    .oneOf([yref('password')], t('forms.create_user.errors.passwords_mismatch')),
  email: string()
    .required(t('forms.create_user.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),
  role: string()
    .required(t('forms.create_user.errors.missing_role')),
  forceChangePassword: boolean()
    .required(t('forms.create_user.errors.missing_force_change_password')),
})
const { handleSubmit, resetForm } = useForm({
  validationSchema: schema,
  initialValues: {
    forceChangePassword: true,
  },
})

const submit = handleSubmit(async (values) => {
  console.log(values)
  // pending.value = true
  // createToken(values.name).then((response) => {
  //   if (response) {
  //     newToken.value = response.payload
  //     emit('completed', response.payload!)
  //   }
  // }).finally(() => pending.value = false)
})

function onHide() {
  newToken.value = null
  resetForm()
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50%"
    :header="t('pages.configuration.users.create_dialog.title')"
    :draggable="false"
    :dismissable-mask="newToken !== null"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="flex flex-col gap-3">
      <p class="text-[--text-color-secondary]">
        {{ t('pages.configuration.users.create_dialog.summary') }}
      </p>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-3 items-end">
        <FormInputText
          id="username"
          name="username"
          :label="t('forms.create_user.fields.username')"
          :placeholder="dev"
          class="w-full"
          autofocus
          @keydown.enter="submit"
        />

        <FormInputText
          id="email"
          name="email"
          :label="t('forms.create_user.fields.email')"
          :placeholder="`${dev}@example.com`"
          type="email"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormInputText
          id="password"
          name="password"
          :label="t('forms.create_user.fields.password')"
          placeholder="••••••••••••••••"
          type="password"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormInputText
          id="confirmPassword"
          name="confirmPassword"
          :label="t('forms.create_user.fields.confirm_password')"
          placeholder="••••••••••••••••"
          type="password"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormDropdown
          id="role"
          name="role"
          :label="t('forms.create_user.fields.role')"
          :placeholder="t('forms.create_user.fields.role_placeholder')"
          :options="roles"
          option-label="label"
          option-value="value"
          :option-disabled="roleDisabled"
          class="w-full"
          @keydown.enter="submit"
        />
      </div>

      <div class="mt-3">
        <FormCheckbox
          id="forceChangePassword"
          name="forceChangePassword"
          :label="t('forms.create_user.fields.force_change_password')"
          :binary="true"
        />
      </div>
    </div>

    <template #footer>
      <PButton
        :label="t('pages.configuration.users.create_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.configuration.users.create_dialog.submit')"
        :loading="pending"
        type="submit"
        @click="submit"
      />
    </template>
  </PDialog>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all .4s cubic-bezier(0.87, 0, 0.13, 1);
}

.slide-left-leave-active {
  position: absolute;
  top: 0;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}
</style>
