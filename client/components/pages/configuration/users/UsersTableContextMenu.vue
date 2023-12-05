<script setup lang="ts">
import { PContextMenu } from '#components'

const cm = inject(UsersTableContextMenuKey)

const { t } = useI18n()
const { user } = useAuth()
const confirm = useConfirm()
const { selectedRow, selectedRowId, unselectRow, refresh } = useUsersTable()

const { activate, deactivate } = useUpdateUserStatus()

const editUserDialog = ref(false)
const deleteUserDialog = ref(false)

function disabled() {
  if (!selectedRowId.value) return true
  return selectedRowId.value === user?.id
}

const statusToggle = computed(() => selectedRow.value?.enabled
  ? {
      label: t('pages.configuration.users.context_menu.deactivate'),
      icon: 'i-tabler-x',
      command() {
        confirm.require({
          message: t('pages.configuration.users.deactivate_dialog.summary'),
          header: t('pages.configuration.users.deactivate_dialog.title'),
          acceptIcon: 'i-tabler-x',
          acceptLabel: t('pages.configuration.users.deactivate_dialog.submit'),
          acceptClass: 'p-button-danger',
          rejectLabel: t('pages.configuration.users.deactivate_dialog.cancel'),
          accept: () => {
            if (!selectedRowId.value) return
            deactivate(selectedRowId.value)
              .then(refresh)
              .finally(unselectRow)
          },
        })
      },
      disabled,
    }
  : {
      label: t('pages.configuration.users.context_menu.activate'),
      icon: 'i-tabler-check',
      command() {
        confirm.require({
          message: t('pages.configuration.users.activate_dialog.summary'),
          header: t('pages.configuration.users.activate_dialog.title'),
          acceptIcon: 'i-tabler-check',
          acceptLabel: t('pages.configuration.users.activate_dialog.submit'),
          rejectLabel: t('pages.configuration.users.activate_dialog.cancel'),
          accept: () => {
            if (!selectedRowId.value) return
            activate(selectedRowId.value)
              .then(refresh)
              .finally(unselectRow)
          },
        })
      },
      disabled,
    },
)
const menuModel = computed(() => [
  {
    label: t('pages.configuration.users.context_menu.edit'),
    icon: 'i-tabler-pencil',
    command() { editUserDialog.value = true },
  },
  statusToggle.value,
  { separator: true },
  {
    label: t('pages.configuration.users.context_menu.delete'),
    icon: 'i-tabler-trash',
    command() { deleteUserDialog.value = true },
    disabled,
    class: 'h-danger',
  },
])
</script>

<template>
  <PContextMenu ref="cm" :model="menuModel" />

  <EditUserDialog v-model:visible="editUserDialog" />
  <DeleteUserDialog v-model:visible="deleteUserDialog" />
  <ConfirmDialog />
</template>
