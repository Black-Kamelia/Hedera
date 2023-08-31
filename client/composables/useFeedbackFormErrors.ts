export function useFeedbackFormErrors() {
  const { m } = useI18n()

  function setFieldErrors(
    errors: any,
    setFieldError: (field: any, message: string | string[] | undefined) => void,
  ) {
    for (const field in errors) {
      setFieldError(field, m(errors[field]))
    }
  }

  return setFieldErrors
}
