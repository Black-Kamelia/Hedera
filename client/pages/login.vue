<script setup lang="ts">
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

function getRedirect() {
  const redirect = currentRoute.value.query.redirect?.toString()
  return redirect && (redirect.startsWith('%2F') || redirect.startsWith('/'))
    ? decodeURIComponent(redirect)
    : '/files'
}

function redirectToApplication() {
  const to = getRedirect()

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
      navigateTo({
        path: '/update-password',
        query: { redirect: getRedirect() },
      }, { replace: true })
    } else {
      redirectToApplication()
    }
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
