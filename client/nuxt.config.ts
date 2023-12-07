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
      link: [
        { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      ],
    },
    layoutTransition: { name: 'layout-in', appear: true },
  },
  ssr: false,
  imports: { // add folders here to auto-import them in your application
    dirs: [
      'stores/**',
      'composables/**',
      'utils/**',
    ],
  },
  components: [{ path: '~/components', pathPrefix: false }],
  runtimeConfig: isDev ? devRuntimeConfig : runtimeConfig,

  // plugins
  modules: [
    '@nuxt/devtools',
    '@vueuse/nuxt',
    '@vue-macros/nuxt',
    '@vee-validate/nuxt',
    '@unocss/nuxt',
    '@nuxtjs/critters',
    '@pinia/nuxt',
    '@pinia-plugin-persistedstate/nuxt',
    '@nuxtjs/color-mode',
    'nuxt-primevue',
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
    vue: {
      script: {
        defineModel: true,
        propsDestructure: true,
      },
    },
  },
  experimental: {
    typedPages: true,
  },
  nitro: {
    devProxy: {
      '/api': {
        target: 'http://localhost:8080/api',
        changeOrigin: true,
        ws: true,
        secure: false,
        prependPath: true,
      },
    },
  },

  // plugin configs
  macros: {
    exportProps: true,
    reactivityTransform: true,
    betterDefine: true,
  },
  veeValidate: {
    autoImports: true,
    componentNames: {
      Form: 'VForm',
      Field: 'VField',
      ErrorMessage: 'VErrorMessage',
      FieldArray: 'VFieldArray',
    },
  },
  colorMode: {
    preference: 'system',
    fallback: 'light',
    classPrefix: '',
    classSuffix: '',
    storageKey: 'color-scheme',
  },
  primevue: {
    options: {
      ripple: false,
      inputStyle: 'filled',
    },
    components: {
      prefix: 'P',
    },
    importPT: {
      as: 'HederaPreset',
      from: resolve(dirname(fileURLToPath(import.meta.url)), '/utils/hederaPreset.ts'),
    },
  },
})
