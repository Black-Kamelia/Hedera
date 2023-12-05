import type { EventBusKey } from '@vueuse/core'

export interface HederaUnknownPayload {}

export type HederaUserConnectedPayload = Omit<UserRepresentationDTO, 'createdAt'> & {
  unlimitedDiskQuota: boolean
}

export interface HederaUserUpdatedPayload extends HederaUserConnectedPayload {}

export interface HederaUserForcefullyLoggedOutPayload {
  userId: string
  reason: string
}

export type HederaConfigurationUpdatedPayload = GlobalConfigurationRepresentationDTO

export type HederaWebsocketPayload =
| { type: 'unknown'; data: HederaUnknownPayload }
| { type: 'user-connected'; data: HederaUserConnectedPayload }
| { type: 'user-updated'; data: HederaUserUpdatedPayload }
| { type: 'user-forcefully-logged-out'; data: HederaUserForcefullyLoggedOutPayload }
| { type: 'configuration-updated'; data: HederaConfigurationUpdatedPayload }

export const WebsocketPacketReceivedEvent: EventBusKey<{
  payload: HederaWebsocketPayload
}> = Symbol('websocket-packet-received')
