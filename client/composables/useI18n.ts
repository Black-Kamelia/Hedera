import { useI18n as _useI18n } from 'vue-i18n'

interface ErrorDTO {
  key: string
  template?: Map<string, string>
}

export default function useI18n(fallback = 'en') {
  const i18n = _useI18n()
  const locale = useLocale(fallback)

  watch(locale, (value) => {
    i18n.locale.value = value
  })

  function e(error: ErrorDTO): String {
    const { key, template } = error

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

  return { locale, t: i18n.t, e }
}
