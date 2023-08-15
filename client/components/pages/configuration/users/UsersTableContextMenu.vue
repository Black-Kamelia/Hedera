<script setup lang="ts">
import { PContextMenu } from '#components'

const emit = defineEmits<{
  (event: 'onEdit'): void
}>()

const cm = inject(UsersTableContextMenuKey)

const { t } = useI18n()
const confirm = useConfirm()
const { selectedRow, selectedRowId, unselectRow, refresh } = useUsersTable()

const { activate, deactivate } = useUpdateUserStatus()
const deleteUser = useDeleteUser()

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
    },
)
const menuModel = computed(() => [
  {
    label: t('pages.configuration.users.context_menu.edit'),
    icon: 'i-tabler-pencil',
    command: () => emit('onEdit'),
  },
  statusToggle.value,
  { separator: true },
  {
    label: t('pages.configuration.users.context_menu.delete'),
    icon: 'i-tabler-trash',
    command() {
      confirm.require({
        message: t('pages.configuration.users.delete_dialog.summary'),
        header: t('pages.configuration.users.delete_dialog.title'),
        acceptIcon: 'i-tabler-trash',
        acceptLabel: t('pages.configuration.users.delete_dialog.submit'),
        acceptClass: 'p-button-danger',
        rejectLabel: t('pages.configuration.users.delete_dialog.cancel'),
        accept: () => {
          if (!selectedRowId.value) return
          deleteUser(selectedRowId.value)
            .then(refresh)
            .finally(unselectRow)
        },
      })
    },
  },
])
</script>

<template>
  <PContextMenu ref="cm" :model="menuModel" />

  <PDynamicDialog />
  <ConfirmDialog />
</template>
