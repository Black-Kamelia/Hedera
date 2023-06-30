<script setup lang="ts">
import { PContextMenu } from '#components'

const emit = defineEmits<{
  (event: 'openFile'): void
  (event: 'renameFile'): void
  (event: 'changeFileVisibility', visibility: string): void
  (event: 'copyLink'): void
  (event: 'downloadFile'): void
  (event: 'deleteFile'): void
}>()
const cm = defineModel<Nullable<CompElement<InstanceType<typeof PContextMenu>>>>('ref')

const { t } = useI18n()
const { isSupported } = useClipboard()

// const cm = ref<Nullable<CompElement<InstanceType<typeof PContextMenu>>>>(null)
const menuModel = computed(() => [
  {
    label: t('pages.files.contextMenu.open'),
    icon: 'i-tabler-external-link',
    command: () => emit('openFile'),
  },
  {
    label: t('pages.files.contextMenu.rename'),
    icon: 'i-tabler-pencil',
    command: () => emit('renameFile'),
  },
  {
    label: t('pages.files.contextMenu.changeVisibility'),
    icon: 'i-tabler-eye',
    items: [
      {
        label: t('pages.files.visibility.public'),
        icon: 'i-tabler-world',
        command: () => emit('changeFileVisibility', 'PUBLIC'),
      },
      {
        label: t('pages.files.visibility.unlisted'),
        icon: 'i-tabler-link',
        command: () => emit('changeFileVisibility', 'UNLISTED'),
      },
      {
        label: t('pages.files.visibility.protected'),
        icon: 'i-tabler-lock',
        disabled: true,
        command: () => emit('changeFileVisibility', 'PROTECTED'),
      },
      {
        label: t('pages.files.visibility.private'),
        icon: 'i-tabler-eye-off',
        command: () => emit('changeFileVisibility', 'PRIVATE'),
      },
    ],
  },
  {
    label: t('pages.files.contextMenu.copyLink'),
    icon: 'i-tabler-link',
    disabled: !isSupported.value,
    command: () => emit('copyLink'),
  },
  {
    label: t('pages.files.contextMenu.download'),
    icon: 'i-tabler-download',
    command: () => emit('downloadFile'),
  },
  { separator: true },
  {
    label: t('pages.files.contextMenu.delete'),
    icon: 'i-tabler-trash',
    command: () => emit('deleteFile'),
  },
])
</script>

<template>
  <PContextMenu ref="cm" :model="menuModel" />
</template>
