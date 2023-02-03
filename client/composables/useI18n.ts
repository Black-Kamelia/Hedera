import type { Ref } from 'vue'
import { useI18n as _useI18n } from 'vue-i18n'

export interface UseI18nComposer {
  locale: Ref<string>
  t: (key: string) => string
}

export default function useI18n(fallback = 'en'): UseI18nComposer {
  const i18n = _useI18n()
  const locale = useLocale(fallback)

  watch(locale, (value) => {
    i18n.locale.value = value
  })

  return { locale, t: i18n.t }
}
