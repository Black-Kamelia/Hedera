import type { EventBusKey } from '@vueuse/core'
import type { FetchError } from 'ofetch'
import type { MessageKeyDTO } from './messages'
import type { Tokens } from '~/stores/useAuth'

export const LoggedInEvent: EventBusKey<{
  tokens?: Tokens
  error?: FetchError
}> = Symbol('logged-in')

export const LoggedOutEvent: EventBusKey<{
  error?: FetchError
}> = Symbol('logged-out')

export const TokensRefreshedEvent: EventBusKey<{
  tokens: Tokens
}> = Symbol('tokens-refreshed')

export const AccessTokenExpiredEvent: EventBusKey<void> = Symbol('access-token-expired')

export const RefreshTokenExpiredEvent: EventBusKey<{
  error?: MessageKeyDTO
}> = Symbol('refresh-token-expired')
