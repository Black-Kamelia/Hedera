<script setup lang="ts">
useTheme()

const toast = useToast()

useEventBus(LoggedInEvent).on((event) => {
  if (!event.success)
    return

  toast.add({
    severity: 'success',
    summary: 'Logged in',
    detail: 'Logged in successfully.',
  })
})

useEventBus(RefreshTokenExpiredEvent).on(() => {
  toast.add({
    severity: 'error',
    summary: 'Session expired',
    detail: 'Your session has expired. Please log in again.',
  })
})

useEventBus(LoggedOutEvent).on(() => {
  toast.add({
    severity: 'success',
    summary: 'Logged out',
    detail: 'Logged out successfully.',
  })
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
