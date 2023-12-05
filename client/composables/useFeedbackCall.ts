import type { MessageDTO } from '~/utils/messages'

export default function useFeedbackCall<
  T,
  F extends (...args: Parameters<F>) => Promise<MessageDTO<T>>,
>(requestFactory: F, ignoreErrors = false) {
  const { t, m } = useI18n()
  const toast = useToast()

  return function call(...args: Parameters<F>): Promise<MessageDTO<T> | void> {
    return requestFactory(...args)
      .then((response: MessageDTO<T>) => {
        toast.add({
          severity: 'success',
          summary: m(response.title),
          detail: { text: response.message ? m(response.message) : null },
          life: 5000,
        })
        return response satisfies MessageDTO<T>
      })
      .catch((error) => {
        if (ignoreErrors) throw error
        if (!error.response) {
          toast.add({
            severity: 'error',
            summary: t('errors.unknown'),
            detail: { text: t('errors.network'), icon: 'i-tabler-world-x' },
            life: 5000,
          })
          throw error
        }
        const { title, message } = error.response._data
        toast.add({
          severity: 'error',
          summary: m(title),
          detail: { text: message ? m(message) : null },
          life: 5000,
        })
        throw error
      })
  }
}
