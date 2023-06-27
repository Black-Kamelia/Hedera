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

  --sidebar-width-open: 19em;
  --sidebar-width-collapsed: 5em;
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

/* Main theme */
@import url('https://fonts.googleapis.com/css2?family=Red+Hat+Display:wght@500;600;700&display=swap');

.dark {
  color-scheme: dark;
}

html, body {
  overflow: hidden;
  height: 100dvh;
  width: 100dvw;
}

/* primevue additions */
body {
  color: var(--text-color);
}

h1, h2, h3, h4, h5, h6 {
  font-family: 'Red Hat Display', sans-serif;
}

.p-input-icon-left > i,
.p-input-icon-right > i {
  margin-top: -12px !important;
}

.p-input-icon-left > .p-inputtext {
  padding-left: calc(1.25rem + 24px);
}

.p-input-icon-right > .p-inputtext {
  padding-right: calc(1.25rem + 24px);
}

.p-toast .p-toast-message .p-toast-message-content {
  border-width: 0 !important;
}

.p-toast .p-toast-message,
.p-message {
  border: 0 none !important;
}

.p-toast-message-icon {
  height: 1em !important;
}

input:focus::placeholder {
  color: transparent;
}

.large-icon {
  width: 2em;
  height: 2em;
}
</style>
