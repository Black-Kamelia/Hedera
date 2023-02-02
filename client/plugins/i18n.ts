import { createI18n } from 'vue-i18n'

const localesFolder = '../locales/'
const locales: [string, any][] = Object.entries(import.meta.glob('../locales/*.y(a)?ml', { eager: true }))
const messages = Object.fromEntries(locales.map(([key, value]) => {
  const yaml = key.endsWith('.yaml')
  return [key.slice(localesFolder.length, yaml ? -5 : -4), value.default]
}))

export default defineNuxtPlugin(({ vueApp }) => {
  vueApp.use(createI18n({
    legacy: false,
    locale: 'en',
    messages,
  }))
})
