<script setup lang="ts">
useTheme()

const toast = useToast()

useEventBus(LoggedInEvent).on((event) => {
  if (event.error)
    return

  toast.add({
    severity: 'success',
    summary: 'Logged in',
    detail: 'Logged in successfully.',
  })
  navigateTo('/')
})

useEventBus(RefreshTokenExpiredEvent).on(() => {
  toast.add({
    severity: 'error',
    summary: 'Session expired',
    detail: 'Your session has expired. Please log in again.',
  })
  navigateTo('/login')
})

useEventBus(LoggedOutEvent).on(() => {
  toast.add({
    severity: 'success',
    summary: 'Logged out',
    detail: 'Logged out successfully.',
  })
  navigateTo('/login')
})
</script>

<template>
  <div font="text" class="p-input-filled">
    <PToast />
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
