import PrimeVue from 'primevue/config'
import PButton from 'primevue/button'

export default defineNuxtPlugin(({ vueApp }) => {
  vueApp.use(PrimeVue, {
    // global config
  })
    .component('PButton', PButton)
})
