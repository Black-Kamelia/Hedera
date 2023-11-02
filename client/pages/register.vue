<script setup lang="ts">
import RegisterForm from '~/components/pages/register/RegisterForm.vue'

const { t } = useI18n()

usePageName(() => t('pages.register.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})

const message = reactive<{
  content: string | null
  severity: 'success' | 'info' | 'warn' | 'error' | undefined
}>({
  content: null,
  severity: undefined,
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
    redirectToApplication()
  }
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <div class="relative">
      <h2 class="font-600 text-3xl mb-3">
        {{ t('pages.register.title') }}
      </h2>
    </div>
  </div>

  <RegisterForm v-model:message="message" />
</template>
