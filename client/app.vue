<script setup lang="ts">
useTheme()
useWebsocketAutoConnect()

const { setTokens, setUser } = useAuth()

useEventBus(WebsocketPacketReceivedEvent).on(({ payload }) => {
  switch (payload.type) {
    case 'user-connected':
    case 'user-updated': {
      setUser(payload.data)
      break
    }
    case 'user-forcefully-logged-out': {
      setTokens(null)
      setUser(null)
      navigateTo(`/login?reason=${encodeURI(payload.data.reason)}`)
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
