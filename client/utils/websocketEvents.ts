import type { EventBusKey } from '@vueuse/core'

export interface HederaUnknownPayload {}

export interface HederaUserUpdatedPayload {
  id: string
  username: string
  email: string
  role: string
  enabled: boolean
  uploadToken: string
}

export type HederaWebsocketPayload =
| { type: 'unknown'; data: HederaUnknownPayload }
| { type: 'user-updated'; data: HederaUserUpdatedPayload }

export const WebsocketPacketReceivedEvent: EventBusKey<{
  payload: HederaWebsocketPayload
}> = Symbol('websocket-packet-received')
