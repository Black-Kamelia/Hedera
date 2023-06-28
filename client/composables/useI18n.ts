import { AxiosError } from 'axios'
import { useI18n as _useI18n } from 'vue-i18n'
import { type ErrorDTO, getDTOFromError } from '~/utils/errors'

export default function useI18n(fallback = 'en') {
  const i18n = _useI18n()
  const locale = useLocale(fallback)

  watch(locale, (value) => {
    i18n.locale.value = value
  })

  function e(error?: ErrorDTO | AxiosError): string {
    const dto = (error instanceof AxiosError ? getDTOFromError(error) : error) ?? { key: 'errors.unknown' }
    const { key, template } = dto

    if (template) {
      // map every value to a translated value
      const mapped = Object.fromEntries(
        Object.entries(template).map(([key, value]) => [key, i18n.t(value)]),
      )
      // return the final translation
      return i18n.t(key, mapped)
    }

    return i18n.t(key)
  }

  return { locale, t: i18n.t, d: i18n.d, e }
}
