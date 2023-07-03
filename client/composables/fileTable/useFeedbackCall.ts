import type { AxiosResponse } from 'axios'

export default function useFeedbackCall<F extends (...args: Parameters<F>) => Promise<AxiosResponse>>(requestFactory: F) {
  const { t, m } = useI18n()
  const toast = useToast()

  return function call(...args: Parameters<F>) {
    return requestFactory(...args)
      .then((response) => {
        toast.add({
          severity: 'success',
          summary: m(response.data.title),
          detail: { text: m(response.data.message) },
          life: 5000,
        })
        return response
      })
      .catch((error) => {
        if (!error.response) {
          toast.add({
            severity: 'error',
            summary: t('errors.unknown'),
            detail: { text: t('errors.network') },
            life: 5000,
          })
          return
        }
        toast.add({
          severity: 'error',
          summary: t('error'), // TODO: get error title from backend
          detail: { text: m(error) },
          life: 5000,
        })
      })
  }
}
