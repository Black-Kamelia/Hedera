<script setup lang="ts">
import { PContextMenu } from '#components'

const cm = inject(FileTableContextMenuKey)

const { t } = useI18n()
const { isSupported } = useClipboard()
const { selectedRow } = useFilesTable()
const confirm = useConfirm()

const changeFileVisibility = useChangeFileVisibility()
const { copyFileLink, copyFileCustomLink } = useCopyFileLink()
const downloadFile = useDownloadFile()
const deleteFile = useDeleteFile()

const editCustomLinkDialog = ref(false)
const editNameDialog = ref(false)

const copyLinkItem = computed(() => {
  if (selectedRow.value?.customLink) {
    return {
      label: t('pages.files.context_menu.copy_link.title'),
      icon: 'i-tabler-clipboard',
      disabled: !isSupported.value,
      items: [
        {
          label: t('pages.files.context_menu.copy_link.original'),
          icon: 'i-tabler-link',
          command: copyFileLink,
        },
        {
          label: t('pages.files.context_menu.copy_link.custom'),
          icon: 'i-tabler-sparkles',
          command: copyFileCustomLink,
        },
      ],
    }
  } else {
    return {
      label: t('pages.files.context_menu.copy_link.title'),
      icon: 'i-tabler-clipboard',
      disabled: !isSupported.value,
      command: copyFileLink,
    }
  }
})
const menuModel = computed(() => [
  {
    label: t('pages.files.context_menu.open'),
    icon: 'i-tabler-external-link',
    command() {
      if (!selectedRow.value) return
      window.open(`/${selectedRow.value.code}`)
    },
  },
  {
    label: t('pages.files.context_menu.rename'),
    icon: 'i-tabler-pencil',
    command: () => editNameDialog.value = true,
  },
  {
    label: selectedRow.value?.customLink
      ? t('pages.files.context_menu.edit_custom_link')
      : t('pages.files.context_menu.set_custom_link'),
    icon: 'i-tabler-sparkles',
    command: () => editCustomLinkDialog.value = true,
  },
  {
    label: t('pages.files.context_menu.change_visibility'),
    icon: 'i-tabler-eye',
    items: [
      {
        label: t('pages.files.visibility.public'),
        icon: 'i-tabler-world',
        command: () => changeFileVisibility('PUBLIC'),
      },
      {
        label: t('pages.files.visibility.unlisted'),
        icon: 'i-tabler-link',
        command: () => changeFileVisibility('UNLISTED'),
      },
      {
        label: t('pages.files.visibility.protected'),
        icon: 'i-tabler-lock',
        disabled: true,
        command: () => changeFileVisibility('PROTECTED'),
      },
      {
        label: t('pages.files.visibility.private'),
        icon: 'i-tabler-eye-off',
        command: () => changeFileVisibility('PRIVATE'),
      },
    ],
  },
  copyLinkItem.value,
  {
    label: t('pages.files.context_menu.download'),
    icon: 'i-tabler-download',
    command: downloadFile,
  },
  { separator: true },
  {
    label: t('pages.files.context_menu.delete'),
    icon: 'i-tabler-trash',
    command() {
      confirm.require({
        message: t('pages.files.delete.warning'),
        header: t('pages.files.delete.title'),
        acceptIcon: 'i-tabler-trash',
        acceptLabel: t('pages.files.delete.submit'),
        acceptClass: 'p-button-danger',
        rejectLabel: t('pages.files.delete.cancel'),
        accept: deleteFile,
      })
    },
  },
])
</script>

<template>
  <PContextMenu ref="cm" :model="menuModel" />

  <ConfirmDialog />
  <EditCustomLinkDialog v-model:visible="editCustomLinkDialog" />
  <EditNameDialog v-model:visible="editNameDialog" />
</template>
