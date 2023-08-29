export default function useFormErrors() {
  const { m } = useI18n()

  function setFieldErrors<T extends string>(
    errors: any,
    setFieldError: (field: T, message: string | string[] | undefined) => void,
  ) {
    for (const field in errors) {
      setFieldError(field as T, m(errors[field]))
    }
  }

  return setFieldErrors
}
