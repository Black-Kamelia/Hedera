import type { EventBusKey } from '@vueuse/core'
import type { Tokens } from '~/stores/useAuth'

export const LoggedInEvent: EventBusKey<{
  success: boolean
  tokens?: Tokens
}> = Symbol('logged-in')

export const LoggedOutEvent: EventBusKey<{
}> = Symbol('logged-out')

export const TokensRefreshedEvent: EventBusKey<{
  tokens: Tokens
}> = Symbol('tokens-refreshed')

export const AccessTokenExpiredEvent: EventBusKey<{
}> = Symbol('access-token-expired')

export const RefreshTokenExpiredEvent: EventBusKey<{
}> = Symbol('refresh-token-expired')
