import { useToast } from 'primevue/usetoast'

export default function useCopyToken() {
  const { t } = useI18n()
  const toast = useToast()
  const { copy, isSupported } = useClipboard()

  function copyToken(token: string) {
    copy(token).then(() => {
      toast.add({
        severity: 'info',
        summary: t('pages.profile.tokens.token_copied'),
        detail: {
          icon: 'i-tabler-clipboard-check',
        },
        life: 5000,
        closable: false,
      })
    })
  }

  return { copyToken, isSupported }
}
