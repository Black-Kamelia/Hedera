<script setup lang="ts">
const { t } = useI18n()

usePageName(() => t('pages.register.title'))
definePageMeta({
  layout: 'auth',
  middleware: ['auth', 'register', 'card-transitions'],
})

useEventBus(LoggedInEvent).on((_, event) => {
  if (!event.error) navigateTo('/files', { replace: true })
})
</script>

<template>
  <div>
    <h2 class="text-center w-full mb-10 font-600 text-3xl">
      {{ t('pages.register.title') }}
    </h2>

    <RegisterForm class="w-175" />
  </div>
</template>
