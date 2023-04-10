<script setup lang="ts">
const props = defineProps<{
  modelValue: Nullable<number>[]
}>()

const emit = defineEmits<{
  (event: 'update:modelValue', digits: Nullable<number>[]): void
}>()

const otpInputRefs = ref<CompElement[]>([])

onMounted(() => {
  otpInputRefs.value[0].$el?.focus()
})

function onKeyDown(event: KeyboardEvent, index: number) {
  const key = event.key

  if (key === 'Backspace' && index > 0) {
    emit('update:modelValue', [...props.modelValue.slice(0, index - 1), null, ...props.modelValue.slice(index)])
    otpInputRefs.value[index - 1].$el?.focus()
    return
  }

  if (key < '0' || key > '9')
    return

  emit('update:modelValue', [...props.modelValue.slice(0, index), parseInt(key), ...props.modelValue.slice(index + 1)])

  if (index < 5)
    otpInputRefs.value[index + 1].$el?.focus()
  else
    otpInputRefs.value[0].$el?.focus()
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
        :value="props.modelValue[n - 1]"
        :name="`otp-${n - 1}`"
        class="p-inputtext-lg w-12 text-center"
        maxlength="1"
        @keydown="onKeyDown($event, n - 1)"
      />
    </div>
  </div>
</template>
