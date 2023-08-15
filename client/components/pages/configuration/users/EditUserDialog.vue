<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'
import FormDropdown from '~/components/input/FormDropdown.vue'

const emit = defineEmits<{
  (event: 'completed', payload: UserRepresentationDTO): void
}>()

const { t } = useI18n()
const dev = getRandomDeveloperUsername()
const { user } = useAuth()
const { selectedRow, selectedRowId, refresh } = useUsersTable()
const updateUser = useUpdateUser()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)

const roles = computed(() => selectedRow.value?.role === 'OWNER'
  ? [
      { label: t('pages.configuration.users.role.owner'), value: 'OWNER', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
    ]
  : [
      { label: t('pages.configuration.users.role.admin'), value: 'ADMIN', icon: 'i-tabler-shield' },
      { label: t('pages.configuration.users.role.regular'), value: 'REGULAR', icon: 'i-tabler-user' },
    ],
)

function roleDisabled(role: string) {
  if (user?.role === 'OWNER') return false
  if (user?.role === 'ADMIN' && role === 'ADMIN') return true
  return true
}

const schema = object({
  username: string()
    .required(t('forms.create_user.errors.missing_username')),
  email: string()
    .required(t('forms.create_user.errors.missing_email'))
    .email(t('forms.create_user.errors.invalid_email')),
  role: string()
    .required(t('forms.create_user.errors.missing_role')),
  // forceChangePassword: boolean()
  //   .required(t('forms.create_user.errors.missing_force_change_password')),
})
const { handleSubmit, resetForm, setValues } = useForm({
  validationSchema: schema,
})

const submit = handleSubmit(async (values) => {
  if (!selectedRow.value) return

  pending.value = true
  updateUser(selectedRow.value.id, values as UserCreationDTO)
    .then(refresh)
    .finally(() => {
      pending.value = false
      visible.value = false
    })
})

watch(visible, (val) => {
  if (val) {
    if (!selectedRow.value) return
    setValues({
      username: selectedRow.value.username,
      email: selectedRow.value.email,
      role: selectedRow.value.role,
    })
  }
})

function onHide() {
  resetForm()
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50% min-w-90% md:min-w-35em"
    :header="t('pages.configuration.users.edit_dialog.title')"
    :draggable="false"
    :dismissable-mask="true"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="flex flex-col gap-3">
      <!--
      <p class="text-[--text-color-secondary] mb-3">
        {{ t('pages.configuration.users.edit_dialog.summary') }}
      </p>
      -->

      <div class="grid grid-cols-1 gap-3 items-end">
        <FormInputText
          id="username"
          name="username"
          :label="t('forms.create_user.fields.username')"
          :placeholder="dev"
          :transform-value="usernameRestrict"
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

        <FormDropdown
          id="role"
          name="role"
          :label="t('forms.create_user.fields.role')"
          :placeholder="t('forms.create_user.fields.role_placeholder')"
          :options="roles"
          :disabled="selectedRow.role === 'OWNER'"
          option-label="label"
          option-value="value"
          :option-disabled="roleDisabled"
          class="w-full"
          @keydown.enter="submit"
        />
      </div>

      <!--
      <div class="mt-3">
        <FormCheckbox
          id="forceChangePassword"
          name="forceChangePassword"
          :label="t('forms.create_user.fields.force_change_password')"
          :binary="true"
        />
      </div>
      -->
    </div>

    <template #footer>
      <PButton
        :label="t('pages.configuration.users.edit_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.configuration.users.edit_dialog.submit')"
        :loading="pending"
        icon="i-tabler-check"
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
