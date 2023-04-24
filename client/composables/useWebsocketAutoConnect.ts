import type { HederaWebsocketPayload } from '~/utils/websocketEvents'

export default function useWebsocketAutoConnect() {
  const { isAuthenticated } = useAuth()
  const { open, close, data, status } = useWebSocket('/ws', {
    immediate: false,
  })
  const websocketPacketReceivedEvent = useEventBus(WebsocketPacketReceivedEvent)

  watch(isAuthenticated, (isAuthenticated) => {
    if (isAuthenticated && status.value === 'CLOSED')
      open()

    if (!isAuthenticated && status.value === 'OPEN')
      close()
  })

  watch(data, (data) => {
    if (!data)
      return

    const payload = JSON.parse(data) as HederaWebsocketPayload
    websocketPacketReceivedEvent.emit({ payload })
  })
}
