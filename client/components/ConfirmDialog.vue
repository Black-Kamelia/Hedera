<script setup lang="ts">
import ConfirmationEventBus from 'primevue/confirmationeventbus/confirmationeventbus.esm'
import type { ConfirmationOptions } from 'primevue/confirmationoptions'

const confirmation = ref<ConfirmationOptions | null>(null)
const visible = ref(false)

const loadingAccept = ref(false)
const loadingReject = ref(false)

function confirmationListener(options: ConfirmationOptions) {
  confirmation.value = options
  if (options.onShow) {
    options.onShow()
  }
  visible.value = true
}

function closeListener() {
  visible.value = false
  confirmation.value = null
}

async function accept() {
  if (confirmation.value?.accept) {
    loadingAccept.value = true
    await confirmation.value.accept()
    loadingAccept.value = false
  }
  visible.value = false
}

async function reject() {
  if (confirmation.value?.reject) {
    loadingReject.value = true
    await confirmation.value.reject()
    loadingReject.value = false
  }
  visible.value = false
}

function onHide() {
  if (confirmation.value?.onHide) {
    confirmation.value.onHide()
  }
  visible.value = false
}

onMounted(() => {
  ConfirmationEventBus.on('confirm', confirmationListener)
  ConfirmationEventBus.on('close', closeListener)
})
onBeforeUnmount(() => {
  ConfirmationEventBus.off('confirm', confirmationListener)
  ConfirmationEventBus.off('close', closeListener)
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    :pt="{
      root: { class: 'max-w-75% xl:max-w-50%' },
      rejectButton: { icon: { class: 'display-none' } },
      message: { class: 'text-[--text-color-secondary]' },
      header: { class: 'min-h-5em' },
    }"
    :header="confirmation?.header"
    :modal="true"
    :draggable="false"
    :closable="!loadingAccept && !loadingReject"
    @update:visible="onHide()"
  >
    <p class="p-text-secondary">
      {{ confirmation?.message }}
    </p>
    <template #footer>
      <PButton
        :label="confirmation?.rejectLabel ?? 'NO'"
        :class="confirmation?.rejectClass"
        class="min-h-3.125em"
        :icon="confirmation?.rejectIcon"
        :loading="loadingReject"
        :disabled="loadingAccept"
        text
        @click="reject()"
      />
      <PButton
        :label="confirmation?.acceptLabel ?? 'YES'"
        :class="confirmation?.acceptClass"
        class="min-h-3.125em"
        :icon="confirmation?.acceptIcon"
        :loading="loadingAccept"
        :disabled="loadingReject"
        @click="accept()"
      />
    </template>
  </PDialog>
</template>
