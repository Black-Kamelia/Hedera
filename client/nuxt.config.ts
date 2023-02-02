import { dirname, resolve } from 'node:path'
import { fileURLToPath } from 'url'
import { transformShortVmodel } from '@vue-macros/short-vmodel'
import VueI18nVitePlugin from '@intlify/unplugin-vue-i18n/vite'

// https://nuxt.com/docs/api/configuration/nuxt-config
export default defineNuxtConfig({
  app: {
    head: {
      title: 'Hedera',
      link: [{ rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' }],
    },
  },
  ssr: false,
  experimental: {
    reactivityTransform: true,
  },
  vue: {
    compilerOptions: {
      nodeTransforms: [transformShortVmodel({ prefix: '::' })],
    },
  },
  vite: {
    plugins: [
      VueI18nVitePlugin({
        include: [resolve(dirname(fileURLToPath(import.meta.url)), 'locales/**')],
      }),
    ],
  },
  imports: {
    dirs: ['stores'],
  },
  modules: [
    '@vueuse/nuxt',
    '@vue-macros/nuxt',
    '@unocss/nuxt',
    '@nuxtjs/critters',
    '@nuxt/image-edge',
    [
      '@pinia/nuxt',
      {
        autoImports: [
          'defineStore',
          ['defineStore', 'definePiniaStore'],
          'storeToRefs',
        ],
      },
    ],
  ],
  css: ['@unocss/reset/antfu.css'],
  macros: {
    exportProps: true,
  },
})
