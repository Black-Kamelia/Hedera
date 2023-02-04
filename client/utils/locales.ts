const localesFolder = '../locales/'
const localeFileExtension = '.yml'
const locales: [string, any][] = Object.entries(import.meta.glob('../locales/*.yml', { eager: true }))

export const messages = Object.fromEntries(locales.map(([key, value]) => {
  return [key.slice(localesFolder.length, -localeFileExtension.length), value.default]
}))

export const availableLocales = Object.keys(messages)
