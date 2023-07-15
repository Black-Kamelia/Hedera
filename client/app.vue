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

const animation = useLocalStorage('animations', true)
</script>

<template>
  <div font="text" class="p-input-filled" :class="{ 'no-animation': !animation }">
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

  --layout-in-duration: 0.4s;
  --layout-out-duration: 0.6s;
}

body:has(.no-animation),
body:has(.no-animation) *,
body:has(.no-animation) *:before,
body:has(.no-animation) *:after {
  transition: none !important;
  -webkit-transition: none !important;
  -moz-transition: none !important;
  -o-transition: none !important;
  transition-duration: 0s !important;
  -webkit-transition-duration: 0s !important;
  -moz-transition-duration: 0s !important;
  -o-transition-duration: 0s !important;
  animation: none !important;
  -moz-animation: none !important;
  -webkit-animation: none !important;
  -o-animation: none !important;
}

.layout-in-enter-active,
.layout-in-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all var(--layout-in-duration) var(--easeInOutExpo);

  .p-card {
    transition: all var(--layout-in-duration) var(--easeInOutExpo);
  }
}

/* MAIN */
.layout-in-enter-from {
  opacity: 0;
  transform: var(--scaleBack);
  filter: blur(5px);
  z-index: 0;
}

/* LOGIN */
.layout-in-leave-to {
  opacity: 0;
  transform: var(--scaleFront);
  filter: blur(10px);
  z-index: 10;

  > .p-card {
    transform: var(--scaleFrontParallax);
    filter: blur(12px);
  }
}

.layout-out-enter-active,
.layout-out-leave-active {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  transition: all var(--layout-out-duration) var(--easeOutExpo);

  .p-card {
    transition: all var(--layout-out-duration) var(--easeOutExpo);
  }
}

/* LOGIN */
.layout-out-enter-from {
  opacity: 0;
  transform: var(--scaleFront);
  filter: blur(10px);
  z-index: 10;

  > .p-card {
    transform: var(--scaleFrontParallax);
    filter: blur(12px);
  }
}

/* MAIN */
.layout-out-leave-to {
  opacity: 0;
  transform: var(--scaleBack);
  filter: blur(5px);
  z-index: 0;
}

/* Main theme */
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

pre {
  font-family: 'JetBrains Mono', monospace;
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

.p-toast {
  width: auto !important;
  max-width: 30em;
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

.p-card:has(.p-datatable) {
  border: 1px solid var(--surface-border);
  border-radius: 8px;
}

.p-contextmenu {
  min-width: 15rem;
  max-width: 30rem;
  width: auto;
  padding: .5rem 0;
}

.p-contextmenu .p-menuitem-separator {
  margin: .5rem 0;
}

.large-icon {
  width: 2em;
  height: 2em;
}
</style>
