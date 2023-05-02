import type { HederaWebsocketPayload } from '~/utils/websocketEvents'

export default function useWebsocketAutoConnect() {
  const websocketPacketReceivedEvent = useEventBus(WebsocketPacketReceivedEvent)

  const appConfig = useRuntimeConfig()
  const host = appConfig.public.websocketUrl || `${location.protocol === 'https:' ? 'wss' : 'ws'}://${location.host}`

  const axios = useAxiosFactory()
  const { isAuthenticated } = useAuth()

  const wsToken = ref<Nullable<string>>(null)
  const webSocketUrl = computed(() => (isAuthenticated.value && wsToken.value)
    ? `${host}/ws?token=${wsToken.value}`
    : undefined)

  const { open, close, status, ws } = useWebSocket(webSocketUrl, { immediate: false })

  async function requestWSToken() {
    const { data } = await axios().get<{ token: string }>('/ws')
    wsToken.value = data.token
  }

  function handleMessage({ data }: MessageEvent) {
    const payload = JSON.parse(data) as HederaWebsocketPayload
    websocketPacketReceivedEvent.emit({ payload })
  }

  async function handleConnection() {
    if (isAuthenticated.value && status.value === 'CLOSED') {
      await requestWSToken()
      open()
      ws.value!.addEventListener('message', handleMessage)
    }

    if (!isAuthenticated.value && status.value === 'OPEN') {
      ws.value!.removeEventListener('message', handleMessage)
      close()
      wsToken.value = null
    }
  }

  onMounted(handleConnection)
  watch(isAuthenticated, handleConnection)
  onUnmounted(() => {
    close()
    ws.value?.removeEventListener('message', handleMessage)
  })
}
