<script setup lang="ts">
const { t } = useI18n()

type State = 'LOGIN' | 'CHANGE_PASSWORD' | 'COMPLETE_OTP'

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
const { currentRoute } = useRouter()
const { isAuthenticated } = storeToRefs(useAuth())

function parseState(value: string): State {
  try {
    if (!isAuthenticated.value) {
      navigateTo('/login', { replace: true })
      return 'LOGIN'
    }
    return value.toUpperCase().replace('-', '_') as State
  } catch (e) {
    return 'LOGIN'
  }
}

const state = ref<State>(parseState(currentRoute.value.query.state as string))
const subtitle = computed(() => {
  switch (state.value) {
    case 'LOGIN': return t('pages.login.title')
    case 'CHANGE_PASSWORD': return t('pages.change_password.title')
    case 'COMPLETE_OTP': return t('pages.two_factor_authentication.title')
  }
})

useEventBus(LoggedInEvent).on((event) => {
  if (!event.error) {
    const user = event.user
    if (user?.forceChangePassword) {
      state.value = 'CHANGE_PASSWORD'
    } else {
      navigateTo('/files', { replace: true })
    }
  }
})
useEventBus(LoggedOutEvent).on((event) => {
  if (event.abortLogin) {
    state.value = 'LOGIN'
    navigateTo('/login', { replace: true })
  }
})
useEventBus(RefreshTokenExpiredEvent).on(() => {
  state.value = 'LOGIN'
  navigateTo('/login?reason=expired', { replace: true })
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
      <LoginForm v-if="state === 'LOGIN'" />
      <PasswordEditionForm v-else-if="state === 'CHANGE_PASSWORD'" />
    </Transition>
  </div>
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
