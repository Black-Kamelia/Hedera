import type { EventBusKey } from '@vueuse/core'
import type { AxiosError } from 'axios'
import type { Tokens } from '~/stores/useAuth'

export const LoggedInEvent: EventBusKey<{
  tokens?: Tokens
  error?: AxiosError
}> = Symbol('logged-in')

export const LoggedOutEvent: EventBusKey<{
}> = Symbol('logged-out')

export const TokensRefreshedEvent: EventBusKey<{
  tokens: Tokens
}> = Symbol('tokens-refreshed')

export const AccessTokenExpiredEvent: EventBusKey<{
  error: AxiosError
}> = Symbol('access-token-expired')

export const RefreshTokenExpiredEvent: EventBusKey<{
  error: AxiosError
}> = Symbol('refresh-token-expired')
