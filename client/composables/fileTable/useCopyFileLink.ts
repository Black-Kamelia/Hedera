export default function useCopyFileLink() {
  const { t } = useI18n()
  const toast = useToast()
  const { copy } = useClipboard()
  const { selectedRow, unselectRow } = useFilesTable()

  function copyLink(linkPart: string) {
    if (!selectedRow.value) return
    copy(`${location.origin}/${linkPart}`)
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

  function copyFileLink() {
    return copyLink(selectedRow.value!.code)
  }

  function copyFileCustomLink() {
    return copyLink(`:${selectedRow.value!.customLink!}`)
  }

  return { copyFileLink, copyFileCustomLink }
}
