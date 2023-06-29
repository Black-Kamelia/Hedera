import { AxiosError } from 'axios'
import { useI18n as _useI18n } from 'vue-i18n'
import type { MessageKeyDTO } from '~/utils/messages'

export default function useI18n(fallback = 'en') {
  const i18n = _useI18n()
  const locale = useLocale(fallback)

  watch(locale, (value) => {
    i18n.locale.value = value
  })

  function m(message?: MessageKeyDTO | AxiosError): string {
    const dto = (message instanceof AxiosError ? getDTOFromError(message) : message) ?? { key: 'errors.unknown' }
    const { key, parameters } = dto

    if (parameters) {
      // map every value to a translated value
      const mapped = Object.fromEntries(
        Object.entries(parameters).map(([key, value]) => [key, i18n.t(value)]),
      )

      // return the final translation
      return i18n.t(key, mapped)
    }

    return i18n.t(key)
  }

  return { locale, t: i18n.t, d: i18n.d, m }
}
