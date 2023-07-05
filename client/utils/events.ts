import type { EventBusKey } from '@vueuse/core'
import type { AxiosError } from 'axios'
import type { MessageKeyDTO } from './messages'
import type { Tokens } from '~/stores/useAuth'

export const LoggedInEvent: EventBusKey<{
  tokens?: Tokens
  error?: AxiosError
}> = Symbol('logged-in')

export const LoggedOutEvent: EventBusKey<{
  error?: AxiosError
}> = Symbol('logged-out')

export const TokensRefreshedEvent: EventBusKey<{
  tokens: Tokens
}> = Symbol('tokens-refreshed')

export const AccessTokenExpiredEvent: EventBusKey<{
  error: AxiosError
}> = Symbol('access-token-expired')

export const RefreshTokenExpiredEvent: EventBusKey<{
  error?: MessageKeyDTO
}> = Symbol('refresh-token-expired')
