import type { AxiosResponse } from 'axios'

export default function useFeedbackCall(requestFactory: (...args: any[]) => Promise<AxiosResponse>) {
  const { t, m } = useI18n()
  const toast = useToast()

  return function call(...args: any[]) {
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
      .catch(error => toast.add({
        severity: 'error',
        summary: t('error'),
        detail: { text: m(error) },
        life: 5000,
      }))
  }
}
