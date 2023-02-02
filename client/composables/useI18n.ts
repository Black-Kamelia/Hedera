import { useI18n as _useI18n } from 'vue-i18n'

export default function useI18n(fallback = 'en') {
  const i18n = _useI18n()
  const locale = useLocale(fallback)

  watch(locale, (value) => {
    i18n.locale.value = value
  })

  return { locale, t: i18n.t }
}
