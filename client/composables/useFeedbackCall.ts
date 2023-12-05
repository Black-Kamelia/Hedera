import type { MessageDTO } from '~/utils/messages'
import useErrorToast from '~/composables/useErrorToast'

export default function useFeedbackCall<
  T,
  F extends (...args: Parameters<F>) => Promise<MessageDTO<T>>,
>(requestFactory: F, ignoreErrors = false) {
  const { m } = useI18n()
  const toast = useToast()
  const handleError = useErrorToast()

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
        handleError(error)
      })
  }
}
