<script setup lang="ts">
const otpInputRefs = ref<CompElement[]>([])

onMounted(() => {
  otpInputRefs.value[0].$el?.focus()
})

const el = ref<Nullable<CompElement>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div ref="el" class="flex items-center justify-center w-screen h-screen">
    <div class="p-card p-10 w-100 sm:w-125 lg:w-150 flex flex-row justify-center">
      <div v-focus-trap class="flex items-center flex-row gap-5">
        <InputText
          v-for="n in 6" :key="n"
          ref="otpInputRefs"
          :name="`otp-${n}`"
          class="p-inputtext-lg otp-input"
          maxlength="1"
        />
      </div>
    </div>
  </div>
</template>

<style scoped>
.otp-input {
  width: 3rem;
  text-align: center;
}
</style>
