import ToastService from 'primevue/toastservice'
import ConfirmationService from 'primevue/confirmationservice'
import DialogService from 'primevue/dialogservice'

export default defineNuxtPlugin(({ vueApp }) => {
  vueApp.use(ToastService)
  vueApp.use(ConfirmationService)
  vueApp.use(DialogService)
})
