<script setup lang="ts">
const { t } = useI18n()

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})

const state = ref(0)
const subtitle = computed(() => {
  if (state.value === 0) return t('pages.login.title')
  if (state.value === 1) return t('pages.change_password.title')
  return t('pages.two_factor_authentication.title')
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <div class="relative">
      <Transition name="slide-left">
        <h2 :key="subtitle" class="font-600 text-3xl mb-3">
          {{ subtitle }}
        </h2>
      </Transition>
    </div>
  </div>

  <div class="relative w-full">
    <Transition name="slide-left">
      <LoginForm v-if="state === 0" />
      <PasswordEditionForm v-else-if="state === 1" />
    </Transition>
  </div>

  <PButton class="absolute top-10 left-10" text @click="state = (state + 1) % 2" />
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active {
  transition: all .4s cubic-bezier(0.87, 0, 0.13, 1);
  width: 100%;
}

.slide-left-leave-active {
  position: absolute;
  top: 0;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}
</style>
