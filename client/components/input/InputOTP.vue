<script setup lang="ts">
export interface InputOTPProps {
  disabled?: boolean
}

export interface InputOTPEmits {
  (event: 'completed', digits: OTP): void
}

const { disabled = false } = defineProps<InputOTPProps>()
const emit = defineEmits<InputOTPEmits>()
const modelValue = defineModel<OTP>({ default: () => Array.from({ length: 6 }).fill(null) })

const otpInputRefs = ref<CompElement[]>([])

onMounted(() => otpInputRefs.value[0].$el?.focus({ preventScroll: true }))

// Add small delay to allow extensions such as KeePassXC to fill the inputs
watchDebounced(
  modelValue,
  (digits) => {
    if (digits.every(digit => digit !== null)) emit('completed', digits)
  },
  { debounce: 5, maxWait: 5 },
)

function doUpdate(digits: OTP) {
  modelValue.value = digits
}

function onInput(e: Event, index: number) {
  const el = e.target as HTMLInputElement
  const value = Number.parseInt(el.value)

  if (Number.isNaN(value)) {
    e.preventDefault()
    return
  }

  doUpdate(OTPWith(modelValue.value, index, value))
}

function onPaste(e: ClipboardEvent, index: number) {
  e.preventDefault()
  const clipboardData = e.clipboardData
  const pastedText = clipboardData?.getData('text')

  if (pastedText === undefined) return

  if (!pastedText.match(OTP_REGEX)) return

  doUpdate(createOTPFromString(pastedText))
  otpInputRefs.value[index].$el?.blur()
}

function onKeyDown(event: KeyboardEvent, index: number) {
  const key = event.key

  // if pressing backspace AND the input is empty then go to the previous input and clear it
  // otherwise clear the current input
  if (key === 'Backspace') {
    event.preventDefault()
    if (modelValue.value[index] === null && index > 0) {
      doUpdate(OTPWith(modelValue.value, index - 1, null))
      otpInputRefs.value[index - 1].$el?.focus()
    } else {
      event.preventDefault()
      doUpdate(OTPWith(modelValue.value, index, null))
    }
  }

  // set the current input to the key pressed if it's a number and go to the next input
  if (key >= '0' && key <= '9') {
    event.preventDefault()
    doUpdate(OTPWith(modelValue.value, index, Number.parseInt(key)))
    if (index < OTP_LENGTH - 1) {
      otpInputRefs.value[index + 1].$el?.focus()
    } else {
      otpInputRefs.value[index].$el?.blur()
    }
  }

  // Navigate between inputs using arrow keys
  if (key === 'ArrowLeft' && index > 0) {
    otpInputRefs.value[index - 1].$el?.focus()
  }
  if (key === 'ArrowRight' && index < OTP_LENGTH - 1) {
    otpInputRefs.value[index + 1].$el?.focus()
  }
}

const el = ref<Nullable<CompElement>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div class="flex items-center flex-row gap-5">
    <PInputText
      v-for="n in OTP_LENGTH" :key="n - 1"
      ref="otpInputRefs"
      :value="modelValue[n - 1]"
      :name="`otp-${n - 1}`"
      :disabled="disabled"
      class="p-inputtext-lg w-12 text-center"
      maxlength="1"
      pattern="[0-9]?"
      inputmode="numeric"
      autocomplete="one-time-code"
      @keydown="onKeyDown($event, n - 1)"
      @paste="onPaste($event, n - 1)"
      @input="onInput($event, n - 1)"
      @keypress.stop.prevent
    />
  </div>
</template>
