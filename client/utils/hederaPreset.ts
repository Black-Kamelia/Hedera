import type { DialogPassThroughOptions } from 'primevue/dialog'

interface GlobalPassThrough {
  dialog: DialogPassThroughOptions
}

const HederaPreset: GlobalPassThrough = {
  dialog: {
    transition: {
      enterActiveClass: 'h-dialog-enter-active',
    },
  },
}

export default HederaPreset
