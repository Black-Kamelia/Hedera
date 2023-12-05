export default function useCopyFileLink() {
  const { t } = useI18n()
  const toast = useToast()
  const { copy } = useClipboard()

  function copyLink(linkPart: string) {
    copy(`${location.origin}${linkPart}`)
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
  }

  function copyFileLink(file: FileRepresentationDTO) {
    return copyLink(`/m/${file.code}`)
  }

  function copyFileCustomLink(file: FileRepresentationDTO) {
    return copyLink(`/c/${file.customLink}`)
  }

  return { copyFileLink, copyFileCustomLink }
}
