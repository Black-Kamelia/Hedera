import type { MessageKeyDTO } from '~/utils/messages'

export function useFeedbackFormErrors() {
  const { m } = useI18n()

  function setFieldErrors<T extends string>(
    errors: any,
    setFieldError: (field: T, message: string | string[] | undefined) => void,
  ) {
    for (const [field, message] of Object.entries(errors)) {
      setFieldError(field as T, m(message as MessageKeyDTO))
    }
  }

  return setFieldErrors
}
