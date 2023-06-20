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

<style lang="scss">
:root {
  --easeInOutExpo: cubic-bezier(0.87, 0, 0.13, 1);
  --easeOutExpo: cubic-bezier(0.16, 1, 0.3, 1);
  --scaleBack: scale(0.95);
  --scaleFront: scale(1.1);
  --scaleFrontParallax: scale(1.2);
}

.layout-in-enter-active,
.layout-in-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all 0.4s var(--easeInOutExpo);

  .p-card {
    transition: all 0.4s var(--easeInOutExpo);
  }
}

/* MAIN */
.layout-in-enter-from {
  opacity: 0;
  transform: var(--scaleBack);
  z-index: 0;
}

/* LOGIN */
.layout-in-leave-to {
  opacity: 0;
  transform: var(--scaleFront);
  filter: blur(10px);
  z-index: 10;

  .p-card {
    transform: var(--scaleFrontParallax);
  }
}

.layout-out-enter-active,
.layout-out-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all 0.6s var(--easeOutExpo);

  .p-card {
    transition: all 0.6s var(--easeOutExpo);
  }
}

/* LOGIN */
.layout-out-enter-from {
  opacity: 0;
  transform: var(--scaleFront);
  filter: blur(10px);
  z-index: 10;

  .p-card {
    transform: var(--scaleFrontParallax);
  }
}

/* MAIN */
.layout-out-leave-to {
  opacity: 0;
  transform: var(--scaleBack);
  z-index: 0;
  //filter: blur(5px);
}
</style>
