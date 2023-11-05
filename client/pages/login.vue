<script setup lang="ts">
// import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})

/*
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

 */
</script>

<template>
  <AuthContainer />
</template>
