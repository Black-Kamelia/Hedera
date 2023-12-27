import { FetchError } from 'ofetch'

export default function useErrorToast() {
  const toast = useToast()
  const { t, m } = useI18n()

  function handleError(error: FetchError) {
    if (!(error instanceof FetchError)) {
      toast.add({
        severity: 'error',
        summary: t('errors.unknown'),
        life: 5000,
      })
      throw error
    }
    if (!error.response) {
      toast.add({
        severity: 'error',
        summary: t('errors.unknown'),
        detail: { text: t('errors.network'), icon: 'i-tabler-world-x' },
        life: 5000,
      })
      return
    }
    const { title, message } = error.response._data
    toast.add({
      severity: 'error',
      summary: m(title),
      detail: { text: message ? m(message) : null },
      life: 5000,
    })
    throw error
  }

  return handleError
}
