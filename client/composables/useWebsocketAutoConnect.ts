import type { HederaWebsocketPayload } from '~/utils/websocketEvents'

export default function useWebsocketAutoConnect() {
  const appConfig = useRuntimeConfig()
  const { isAuthenticated, tokens } = useAuth()
  const { data: wsTokenData, error, execute } = useAPI<{ token: string }>('/ws', { method: 'GET' })
  const webSocketUrl = computed(() => {
    return `${appConfig.public.websocketUrl}?token=${wsTokenData.value?.token}`
  })
  const { open, close, data, status } = useWebSocket(webSocketUrl, {
    immediate: false,
  })
  const websocketPacketReceivedEvent = useEventBus(WebsocketPacketReceivedEvent)

  async function handleConnection(isAuthenticated: boolean) {
    if (isAuthenticated && status.value === 'CLOSED') {
      await execute()
      open()
    }

    if (!isAuthenticated && status.value === 'OPEN')
      close()
  }

  console.log('websocketUrl', webSocketUrl.value)

  // onMounted(() => handleConnection(isAuthenticated.value))

  watch(isAuthenticated, handleConnection)

  watch(data, (data) => {
    if (!data)
      return

    const payload = JSON.parse(data) as HederaWebsocketPayload
    websocketPacketReceivedEvent.emit({ payload })
  })
}
