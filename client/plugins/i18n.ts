import { createI18n } from 'vue-i18n'

export default defineNuxtPlugin(({ vueApp }) => {
  const locale = useLocale()
  const i18n = createI18n({
    legacy: false,
    locale: locale.value,
    allowComposition: true,
    fallbackLocale: 'en',
    messages,
  }) as { install: (app: typeof vueApp) => void }
  vueApp.use(i18n)
})
