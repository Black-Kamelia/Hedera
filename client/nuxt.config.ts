import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'node:url'
import VueI18nVitePlugin from '@intlify/unplugin-vue-i18n/vite'
import { transformShortVmodel } from '@vue-macros/short-vmodel'
import runtimeConfig from './env.config.json'
import devRuntimeConfig from './env.dev.config.json'

const isDev = process.env.NODE_ENV === 'development'

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  // general
  app: {
    head: {
      title: 'Hedera',
      link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
    },
  },
  ssr: false,
  css: [
    '@unocss/reset/tailwind.css',
    'primevue/resources/primevue.css',
  ],
  imports: { // add folders here to auto-import them in your application
    dirs: [
      'stores/**',
      'composables/**',
      'utils/**',
    ],
  },
  components: [{ path: '~/components', pathPrefix: false }],
  runtimeConfig: isDev ? devRuntimeConfig : runtimeConfig,
  routeRules: {
    '/': { redirect: '/files' },
  },

  // plugins
  modules: [
    '@nuxt/devtools',
    '@vueuse/nuxt',
    '@vue-macros/nuxt',
    '@unocss/nuxt',
    '@nuxtjs/critters',
    '@pinia/nuxt',
    '@pinia-plugin-persistedstate/nuxt',
    '@nuxtjs/color-mode',
    '@notkamui/nuxt-primevue',
  ],
  devtools: {
    enabled: true,
    vscode: {},
  },
  vue: {
    compilerOptions: {
      nodeTransforms: [transformShortVmodel({ prefix: '::' })],
    },
  },
  vite: {
    plugins: [
      VueI18nVitePlugin({
        include: [resolve(dirname(fileURLToPath(import.meta.url)), './locales/*.yml')],
      }),
    ],
  },

  // plugin configs
  macros: {
    exportProps: true,
    reactivityTransform: true,
  },
  pinia: {
    autoImports: [
      'defineStore',
      ['defineStore', 'definePiniaStore'],
      'storeToRefs',
    ],
  },
  colorMode: {
    preference: 'system',
    fallback: 'light',
    classPrefix: '',
    classSuffix: '',
    storageKey: 'color-scheme',
  },
  primevue: {
    ripple: false,
    inputStyle: 'filled',
  },
})
