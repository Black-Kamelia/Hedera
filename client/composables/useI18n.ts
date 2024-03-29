import type { DateTimeOptions } from 'vue-i18n'
import { useI18n as _useI18n } from 'vue-i18n'
import fr from 'public/assets/locale/fr.json'
import en from 'public/assets/locale/en.json'
import type { MessageKeyDTO } from '~/utils/messages'

export default function useI18n(fallback = 'en') {
  const i18n = _useI18n()
  const locale = useLocale(fallback)
  const { preferredTimeStyle, preferredDateStyle } = useUserSettings()
  const primeVue = usePrimeVue()

  function updatePrimeVueLocale(newLocale: string) {
    switch (newLocale) {
      case 'fr':
        primeVue.config.locale = fr
        break
      case 'en':
        primeVue.config.locale = en
        break
    }
  }

  watch(locale, (value) => {
    i18n.locale.value = value
    updatePrimeVueLocale(value)
  }, { immediate: true })

  function d(date: Date | string | number, options?: DateTimeOptions): string {
    if (!options) {
      return i18n.d(date, {
        dateStyle: preferredDateStyle.toLowerCase(),
        timeStyle: preferredTimeStyle.toLowerCase(),
      } as DateTimeOptions)
    }
    return i18n.d(date, {
      dateStyle: options.dateStyle?.toLowerCase(),
      timeStyle: options.timeStyle?.toLowerCase(),
    } as DateTimeOptions)
  }

  function m(message?: MessageKeyDTO): string {
    const dto = message ?? { key: 'errors.unknown' }
    const { key, parameters } = dto

    if (parameters) {
      // map every value to a translated value
      const mapped = Object.fromEntries(
        Object.entries(parameters).map(([key, value]) => [key, m(value)]),
      )

      if (Object.keys(parameters).some(key => key === 'success' || key === 'fail')) {
        const count = Number.parseInt(parameters.success?.key ?? parameters.fail?.key ?? '0')
        return i18n.t(key, mapped, count)
      }
      return i18n.t(key, mapped)
    }

    return i18n.t(key)
  }

  return { locale, t: i18n.t, d, m, n: i18n.n }
}
