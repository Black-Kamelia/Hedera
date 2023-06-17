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
  navigateTo('/login?reason=expired')
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

<style>
.layout-in-enter-active,
.layout-in-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all 0.4s cubic-bezier(0.87, 0, 0.13, 1);
}
.layout-in-enter-from {
  opacity: 0;
  transform: scale(0.95);
  filter: blur(5px);
}
.layout-in-leave-to {
  opacity: 0;
  transform: scale(1.15);
  filter: blur(10px);
}

.layout-out-enter-active,
.layout-out-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all 0.4s cubic-bezier(0.87, 0, 0.13, 1);
}
.layout-out-enter-from {
  opacity: 0;
  transform: scale(1.15);
  filter: blur(10px);
}
.layout-out-leave-to {
  opacity: 0;
  transform: scale(0.95);
  filter: blur(5px);
}
</style>
