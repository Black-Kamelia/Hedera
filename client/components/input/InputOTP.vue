<script setup lang="ts">
const otpInputRefs = ref([])

onMounted(() => {
  nextTick(() => {
    otpInputRefs.value[0].focus()
  })
})
</script>

<template>
  <div class="flex items-center justify-center w-screen h-screen">
    <div class="p-card p-10 w-100 sm:w-125 lg:w-150">
      <div class="flex items-center flex-row gap-5">
        <InputText
          v-for="n in 6" :key="n" :ref="el => { otpInputRefs[n] = el }" class="p-inputtext-lg otp-input"
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
