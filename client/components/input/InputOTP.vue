<script setup lang="ts">
export interface InputOTPProps {
  disabled?: boolean
}

export interface InputOTPEmits {
  (event: 'completed', digits: Nullable<number>[]): void
}

const emit = defineEmits<InputOTPEmits>()

const { disabled = false } = definePropsRefs<InputOTPProps>()

const { modelValue } = defineModels<{
  modelValue: Nullable<number>[]
}>()

const otpInputRefs = ref<CompElement[]>([])

onMounted(() => {
  otpInputRefs.value[0].$el?.focus()
})

function doUpdate(digits: Nullable<number>[]) {
  modelValue.value = digits
  if (digits.every(digit => digit !== null))
    emit('completed', digits)
}

function onPaste(e: ClipboardEvent, index: number) {
  e.preventDefault()
  const clipboardData = e.clipboardData
  const pastedText = clipboardData?.getData('text')

  if (pastedText === undefined)
    return

  if (!pastedText.match(/^\d{6}$/))
    return

  const digits = pastedText.split('').map(digit => parseInt(digit))

  doUpdate(digits)
  otpInputRefs.value[index].$el?.blur()
}

function onInput(e: Event, index: number) {
  const el = e.target as HTMLInputElement
  const value = el.value[0]

  if (isNaN(parseInt(value)))
    return

  // if the input is empty then go to the previous input and clear it
  if (value.length === 0) {
    doUpdate(replacedAt(modelValue.value, index, null))
    otpInputRefs.value[index - 1].$el?.focus()
    return
  }

  // if the input is not empty then go to the next input
  doUpdate(replacedAt(modelValue.value, index, parseInt(value)))
  if (index < 5)
    otpInputRefs.value[index + 1].$el?.focus()
  else
    otpInputRefs.value[index].$el?.blur()
}

function onKeyDown(event: KeyboardEvent, index: number) {
  const key = event.key

  // if pressing backspace AND the input is empty then go to the previous input and clear it
  // otherwise clear the current input
  if (key === 'Backspace') {
    event.preventDefault()
    if (modelValue.value[index] === null && index > 0) {
      doUpdate(replacedAt(modelValue.value, index - 1, null))
      otpInputRefs.value[index - 1].$el?.focus()
    }
    else {
      event.preventDefault()
      doUpdate(replacedAt(modelValue.value, index, null))
    }
  }

  // Navigate between inputs using arrow keys
  if (key === 'ArrowLeft' && index > 0)
    otpInputRefs.value[index - 1].$el?.focus()
  if (key === 'ArrowRight' && index < 5)
    otpInputRefs.value[index + 1].$el?.focus()
}

const el = ref<Nullable<CompElement>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div ref="el" class="p-card p-10 w-100 sm:w-125 lg:w-150 flex flex-row justify-center">
    <div v-focus-trap class="flex items-center flex-row gap-5">
      <InputText
        v-for="n in 6" :key="n - 1"
        ref="otpInputRefs"
        :value="modelValue[n - 1]"
        :name="`otp-${n - 1}`"
        :disabled="disabled"
        class="p-inputtext-lg w-12 text-center"
        maxlength="1"
        @input="onInput($event, n - 1)"
        @keydown="onKeyDown($event, n - 1)"
        @paste="onPaste($event, n - 1)"
      />
    </div>
  </div>
</template>
