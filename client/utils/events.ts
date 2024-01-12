import type { EventBusKey } from '@vueuse/core'
import type { FetchError } from 'ofetch'
import type { MessageKeyDTO } from './messages'
import type { Tokens } from '~/stores/useAuth'

export const LoggedInEvent: EventBusKey<{
  tokens?: Tokens
  user?: UserRepresentationDTO
  error?: FetchError
}> = Symbol('logged-in')

export const ForcePasswordChangeDoneEvent: EventBusKey<{}>
  = Symbol('force-password-change-done')

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

export const FileDeletedEvent: EventBusKey<void> = Symbol('file-deleted')

export const FilesTableDoubleClickEvent: EventBusKey<{
  file: FileRepresentationDTO
}> = Symbol('files-table-double-click')
