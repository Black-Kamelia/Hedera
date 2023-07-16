export default function useCopyFileLink() {
  const { t } = useI18n()
  const toast = useToast()
  const { copy } = useClipboard()
  const { selectedRow, unselectRow } = useFilesTable()

  return function copyFileLink() {
    if (!selectedRow.value) return
    copy(`${location.origin}/${selectedRow.value!.code}`)
      .then(() => {
        toast.add({
          severity: 'info',
          summary: t('pages.files.link_copied'),
          detail: {
            icon: 'i-tabler-clipboard-check',
          },
          life: 5000,
          closable: false,
        })
      })
      .then(unselectRow)
  }
}
