import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'url'
import VueI18nVitePlugin from '@intlify/unplugin-vue-i18n/vite'
import { transformShortVmodel } from '@vue-macros/short-vmodel'

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
    '@unocss/reset/antfu.css',
    'primevue/resources/themes/lara-light-blue/theme.css',
    'primevue/resources/primevue.css',
  ],
  sourcemap: {
    server: true,
    client: false,
  },
  imports: { dirs: ['stores'] },
  components: [{ path: '~/components', pathPrefix: false }],

  // plugins
  modules: [
    '@vueuse/nuxt',
    '@vue-macros/nuxt',
    '@unocss/nuxt',
    '@nuxtjs/critters',
    '@nuxt/image-edge',
    '@pinia/nuxt',
    '@nuxtjs/color-mode',
    '@notkamui/nuxt-primevue',
    '@formkit/nuxt',
  ],
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
    ripple: true,
  },
})
