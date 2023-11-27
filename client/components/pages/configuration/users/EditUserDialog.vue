<script lang="ts" setup>
import { boolean, number, object, string } from 'yup'

const { t } = useI18n()
const dev = getRandomDeveloperUsername()
const { user } = useAuth()
const { selectedRow, refresh } = useUsersTable()
const updateUser = useUpdateUser()
const setFieldErrors = useFeedbackFormErrors()

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

const schema = object({
  username: string()
    .required(t('forms.edit_user.errors.missing_username')),
  email: string()
    .required(t('forms.edit_user.errors.missing_email'))
    .email(t('forms.edit_user.errors.invalid_email')),
  role: string()
    .required(t('forms.edit_user.errors.missing_role')),
  unlimitedDiskQuota: boolean()
    .required(t('forms.edit_user.errors.missing_unlimited_disk_quota')),
  diskQuota: number()
    .typeError(t('forms.edit_user.errors.invalid_disk_quota'))
    .when('unlimitedDiskQuota', {
      is: true,
      then: schema => schema.notRequired(),
      otherwise: schema => schema.required(t('forms.edit_user.errors.missing_disk_quota')),
    }),
})
const { handleSubmit, resetForm, setFieldValue, setValues, setFieldError, errors } = useForm({
  validationSchema: schema,
})

const unlimitedQuota = ref(false)
const quotaPlaceholder = computed(() => {
  if (unlimitedQuota.value) return t('forms.edit_user.fields.disk_quota_placeholder_unlimited')
  return t('forms.edit_user.fields.disk_quota_placeholder')
})
watch(unlimitedQuota, (val) => {
  if (val) setFieldValue('diskQuota', null, errors.value.diskQuota !== undefined)
})

const submit = handleSubmit(async (values) => {
  if (!selectedRow.value) return
  pending.value = true

  if (values.unlimitedDiskQuota) {
    values.diskQuota = -1
  }

  updateUser(selectedRow.value.id, values as UserCreationDTO)
    .then(() => {
      visible.value = false
      refresh()
    })
    .catch((err) => {
      setFieldErrors(err.response._data.fields, setFieldError)
    })
    .finally(() => {
      pending.value = false
    })
})

watch(visible, (val) => {
  if (val) {
    if (!selectedRow.value) return

    const unlimited = selectedRow.value.maximumDiskQuota === -1
    setValues({
      username: selectedRow.value.username,
      email: selectedRow.value.email,
      role: selectedRow.value.role,
      diskQuota: unlimited ? null : selectedRow.value.maximumDiskQuota,
      unlimitedDiskQuota: unlimited,
    })
    unlimitedQuota.value = unlimited
  }
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-100% sm:max-w-75% xl:max-w-50% min-w-90% md:min-w-35em"
    :header="t('pages.configuration.users.edit_dialog.title')"
    :draggable="false"
    :pt="{ content: { class: 'overflow-hidden' } }"
    @hide="resetForm()"
  >
    <div class="flex flex-col gap-3">
      <PMessage
        severity="info"
        class="mb-3"
        :closable="false" icon="i-tabler-info-circle-filled"
        :pt="{ root: { style: { marginTop: 0 } } }"
      >
        {{ t('pages.configuration.users.edit_dialog.password_summary') }}
        <NuxtLink v-if="selectedRow?.id === user?.id" class="underline" to="/profile/security">
          {{ t('pages.configuration.users.edit_dialog.password_change_yours') }}
        </NuxtLink>
      </PMessage>

      <div class="grid grid-cols-1 md:grid-cols-2 gap-3 items-start">
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
          :disabled="selectedRow?.role === 'OWNER' || user?.role !== 'OWNER'"
          option-label="label"
          option-value="value"
          class="w-full"
          @keydown.enter="submit"
        />

        <FormInputFileSize
          id="diskQuota"
          name="diskQuota"
          :label="t('forms.create_user.fields.disk_quota')"
          :placeholder="quotaPlaceholder"
          :disabled="unlimitedQuota"
          class="w-full"
          @keydown.enter="submit"
        />
      </div>

      <div class="mt-3 flex flex-col gap-3">
        <FormCheckbox
          id="unlimitedDiskQuota"
          v-model="unlimitedQuota"
          name="unlimitedDiskQuota"
          :label="t('forms.create_user.fields.unlimited_disk_quota')"
          binary
        />
      </div>
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
