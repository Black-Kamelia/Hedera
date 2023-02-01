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
  imports: {
    dirs: ['stores'],
  },
  modules: [
    '@vueuse/nuxt',
    '@vue-macros/nuxt',
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
  macros: {
    exportProps: true,
  },
})
