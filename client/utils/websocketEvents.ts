import type { EventBusKey } from '@vueuse/core'

export interface HederaUnknownPayload {}

export interface HederaUserConnectedPayload {
  id: string
  username: string
  email: string
  role: string
  enabled: boolean
  forceChangePassword: boolean
}

export interface HederaUserUpdatedPayload extends HederaUserConnectedPayload {}

export interface HederaUserForcefullyLoggedOutPayload {
  userId: string
  reason: string
}

export type HederaWebsocketPayload =
| { type: 'unknown'; data: HederaUnknownPayload }
| { type: 'user-connected'; data: HederaUserConnectedPayload }
| { type: 'user-updated'; data: HederaUserUpdatedPayload }
| { type: 'user-forcefully-logged-out'; data: HederaUserForcefullyLoggedOutPayload }

export const WebsocketPacketReceivedEvent: EventBusKey<{
  payload: HederaWebsocketPayload
}> = Symbol('websocket-packet-received')
