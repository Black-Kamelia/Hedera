<script setup lang="ts">
useTheme()
useWebsocketAutoConnect()

useEventBus(WebsocketPacketReceivedEvent).on(({ payload }) => {
  switch (payload.type) {
    case 'unknown':
      console.log('Unknown packet received', payload) // eslint-disable-line no-console
      break
    case 'user-updated': {
      // type-safe access to payload.data
      const { username, email } = payload.data
      console.log('User updated', username, email) // eslint-disable-line no-console
      break
    }
  }
})
useEventBus(RefreshTokenExpiredEvent).on(() => {
  navigateTo('/login?expired')
})
useEventBus(LoggedOutEvent).on(() => {
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
