<script setup lang="ts">
import { PContextMenu } from '#components'

const cm = inject(UsersTableContextMenuKey)

const { t } = useI18n()
const confirm = useConfirm()

const deleteUser = useDeleteUser()

const menuModel = computed(() => [
  {
    // label: t('pages.configuration.users.context_menu.edit'),
    label: 'Éditer',
    icon: 'i-tabler-pencil',
    // command: renameFile,
  },
  {
    // label: t('pages.configuration.users.context_menu.deactivate'),
    label: 'Désactiver',
    icon: 'i-tabler-x',
  },
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
          // deleteUser(selectedRow.value.id)
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
