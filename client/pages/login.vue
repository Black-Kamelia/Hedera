<script setup lang="ts">
import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()

type State = 'LOGIN' | 'CHANGE_PASSWORD' | 'COMPLETE_OTP'

function stateToIndex(state: State) {
  switch (state) {
    case 'LOGIN':
      return 0
    case 'CHANGE_PASSWORD':
      return 1
    case 'COMPLETE_OTP':
      return 2
  }
}

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
const { currentRoute } = useRouter()
const { isAuthenticated } = storeToRefs(useAuth())

function parseState(value: string): State {
  if (!isAuthenticated.value) return 'LOGIN'
  return value.toUpperCase().replace('-', '_') as State
}

const state = ref<State>(parseState(currentRoute.value.query.state as string))
const stateHistory = useRefHistory(state, { capacity: 2 })
const stateTransition = computed(() => {
  const history = stateHistory.history.value
  if (history.length < 2) return 'slide-left'
  const [next, prev] = history
  return stateToIndex(prev.snapshot) < stateToIndex(next.snapshot) ? 'slide-left' : 'slide-right'
})

const subtitle = computed(() => {
  switch (state.value) {
    case 'LOGIN': return t('pages.login.title')
    case 'CHANGE_PASSWORD': return t('pages.change_password.title')
    case 'COMPLETE_OTP': return t('pages.two_factor_authentication.title')
  }
})
const message = reactive<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
})

const loginFormRef = ref()
const changePasswordFormRef = ref()
const { height: loginHeight } = useElementSize(loginFormRef)
const { height: changePasswordHeight } = useElementSize(changePasswordFormRef)
const cardTransition = ref(false)
const cardHeight = computed(() => Math.max(loginHeight.value, changePasswordHeight.value))
watch([state], () => cardTransition.value = true)

onMounted(() => {
  const query = currentRoute.value.query
  const params = Object.keys(query)
  if (params.includes('reason')) {
    message.content = t(`pages.login.reasons.${query.reason}`)
    message.severity = 'warn'
  }
})

function redirectToApplication() {
  const redirect = useRoute().query.redirect?.toString()
  const to = redirect && (redirect.startsWith('%2F') || redirect.startsWith('/'))
    ? decodeURIComponent(redirect)
    : '/files'

  try {
    navigateTo(to, { replace: true })
  } catch {
    navigateTo('/files', { replace: true })
  }
}

useEventBus(LoggedInEvent).on((event) => {
  if (!event.error) {
    const user = event.user
    message.content = null
    message.severity = undefined
    if (user?.forceChangePassword) {
      state.value = 'CHANGE_PASSWORD'
    } else {
      redirectToApplication()
    }
  }
})
useEventBus(ForcePasswordChangeDoneEvent).on(() => {
  redirectToApplication()
})
useEventBus(LoggedOutEvent).on((event) => {
  if (event?.abortLogin) {
    state.value = 'LOGIN'
    navigateTo('/login', { replace: true })
  }
})
useEventBus(RefreshTokenExpiredEvent).on(() => {
  state.value = 'LOGIN'
  message.content = t('pages.login.reasons.expired')
  message.severity = 'warn'
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <div class="relative">
      <Transition :name="stateTransition">
        <h2 :key="subtitle" class="font-600 text-3xl mb-3">
          {{ subtitle }}
        </h2>
      </Transition>
    </div>
  </div>

  <div class="relative w-full" :class="{ 'h-transition': cardTransition }" :style="{ height: `${cardHeight}px` }">
    <Transition :name="stateTransition" @after-enter="cardTransition = false">
      <LoginForm v-if="state === 'LOGIN'" ref="loginFormRef" v-model:message="message" />
      <PasswordEditionForm v-else-if="state === 'CHANGE_PASSWORD'" ref="changePasswordFormRef" />
    </Transition>
  </div>
</template>

<style scoped>
.h-transition {
  transition: height .4s cubic-bezier(0.87, 0, 0.13, 1);
}

.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all .4s cubic-bezier(0.87, 0, 0.13, 1);
  width: 100%;
}

.slide-left-leave-active,
.slide-right-leave-active {
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

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
