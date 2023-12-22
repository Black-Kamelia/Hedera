<script setup lang="ts">
import { ForcePasswordChangeDoneEvent } from '~/utils/events'

const { t } = useI18n()

usePageName(() => t('pages.change_password.title'))
definePageMeta({
  layout: 'auth',
  middleware: ['auth', 'card-transitions'],
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

useEventBus(ForcePasswordChangeDoneEvent).on(() => {
  redirectToApplication()
})
useEventBus(RefreshTokenExpiredEvent).on(() => {
  navigateTo({
    path: '/login',
    query: { reason: 'expired' },
  }, { replace: true })
})
useWebsocketEvent('user-forcefully-logged-out', (event) => {
  navigateTo({
    path: '/login',
    query: { reason: event.reason ?? 'session_terminated' },
  }, { replace: true })
})
</script>

<template>
  <div>
    <h2 class="text-center w-full mb-10 font-600 text-3xl">
      {{ t('pages.change_password.title') }}
    </h2>

    <PasswordEditionForm class="w-125" />
  </div>
</template>
