<script setup lang="ts">
import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'auth',
  middleware: ['auth', 'card-transitions'],
})

const { currentRoute } = useRouter()

const message = reactive<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
})

onMounted(() => {
  const query = currentRoute.value.query
  const params = Object.keys(query)
  if (params.includes('reason')) {
    message.content = t(`pages.login.reasons.${query.reason}`)
    message.severity = 'warn'
    if (query.reason === 'registration_disabled') {
      message.severity = 'error'
    }
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
      // state.value = 'CHANGE_PASSWORD'
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
    // state.value = 'LOGIN'
    navigateTo('/login', { replace: true })
  }
})
useEventBus(RefreshTokenExpiredEvent).on(() => {
  // state.value = 'LOGIN'
  message.content = t('pages.login.reasons.expired')
  message.severity = 'warn'
})
useWebsocketEvent('user-forcefully-logged-out', (event) => {
  // state.value = 'LOGIN'
  message.content = t('pages.login.reasons.session_terminated')
  message.severity = 'warn'
  if (event.reason) {
    message.content = t(`pages.login.reasons.${event.reason}`)
  }
})
</script>

<template>
  <div>
    <h2 class="text-center w-full mb-10 font-600 text-3xl">
      {{ t('pages.login.title') }}
    </h2>

    <LoginForm v-model:message="message" class="w-125" />
  </div>
</template>
