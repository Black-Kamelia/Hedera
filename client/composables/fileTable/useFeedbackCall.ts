import type { MessageDTO } from '~/utils/messages'

export default function useFeedbackCall<
  T,
  F extends (...args: Parameters<F>) => Promise<MessageDTO<T>>,
>(requestFactory: F) {
  const { t, m } = useI18n()
  const toast = useToast()

  return function call(...args: Parameters<F>): Promise<MessageDTO<T> | void> {
    return requestFactory(...args)
      .then((response: MessageDTO<T>) => {
        toast.add({
          severity: 'success',
          summary: m(response.title),
          detail: { text: m(response.message) },
          life: 5000,
        })
        return response as MessageDTO<T>
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
