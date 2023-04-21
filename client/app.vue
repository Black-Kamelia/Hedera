<script setup lang="ts">
useTheme()

const toast = useToast()

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
    <PToast close-icon="i-tabler-x" error-icon="i-tabler-alert-circle-filled" />
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
