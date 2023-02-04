import { createI18n } from 'vue-i18n'

export default defineNuxtPlugin(({ vueApp }) => {
  const locale = $(useLocale())
  const i18n = createI18n({
    legacy: false,
    allowComposition: true,
    locale,
    fallbackLocale: 'en',
    messages,
  })
  vueApp.use(i18n)
})
