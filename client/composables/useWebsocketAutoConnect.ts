import type { HederaWebsocketPayload } from '~/utils/websocketEvents'

export default function useWebsocketAutoConnect() {
  const websocketPacketReceivedEvent = useEventBus(WebsocketPacketReceivedEvent)

  const appConfig = useRuntimeConfig()
  const host = appConfig.public.websocketUrl || `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}`

  const { isAuthenticated, tokens } = storeToRefs(useAuth())

  // will redirect to /login if not authenticated
  const { data: tokenResponse, execute } = useLazyFetchAPI<{ token: string }>('/ws', { immediate: false })
  const webSocketUrl = computed(() => tokenResponse.value && isAuthenticated.value
    ? `${host}/ws?token=${tokenResponse.value.token}&session=${tokens.value?.accessToken}`
    : undefined)

  const { open, close } = useWebSocket(webSocketUrl, {
    immediate: false, // let the watcher below handle the connection
    autoReconnect: {
      retries: 3,
      delay: 3000,
      async onFailed() {
        await openConnection()
      },
    },
    heartbeat: true,
    autoClose: false,
    onMessage(_, { data }) {
      if (data === 'pong') return
      const payload = JSON.parse(data) as HederaWebsocketPayload
      websocketPacketReceivedEvent.emit({ payload })
    },
    onDisconnected() {
      // TODO: Feedback to the user
      console.error('Lost connection to the server')
    },
  })

  async function openConnection() {
    await sleep(1000) // wait a few ticks to avoid contention issues
    await execute() // will redirect to /login if needed
    open()
  }

  watchEffect(async () => {
    if (isAuthenticated.value) {
      await openConnection()
    } else {
      close()
    }
  })
}
