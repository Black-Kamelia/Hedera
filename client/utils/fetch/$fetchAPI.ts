import type { NitroFetchRequest } from 'nitropack'
import { $fetch } from 'ofetch'
import type { FetchAPIOptions } from './api/types'
import type { Tokens } from '~/stores/useAuth'

const $fetchRefresh = configureRefreshFetch({
  fetch: $fetch,
  refreshToken(fetch) {
    const accessTokenExpiredEvent = useEventBus(AccessTokenExpiredEvent)
    const tokensRefreshedEvent = useEventBus(TokensRefreshedEvent)
    const refreshTokenExpiredEvent = useEventBus(RefreshTokenExpiredEvent)

    accessTokenExpiredEvent.emit()

    const auth = useAuth()

    return fetch<Tokens>('/api/refresh', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${auth.tokens?.refreshToken.token}`,
        'Access-Control-Allow-Origin': '*',
      },
    }).then((response) => {
      auth.setTokens(response)
      tokensRefreshedEvent.emit({ tokens: response })
    }).catch((error) => {
      auth.setUser(null)
      auth.setTokens(null)
      refreshTokenExpiredEvent.emit({ error: error.response._data })
      throw error
    })
  },
  shouldRefreshToken(e) {
    const { isAuthenticated } = useAuth()
    const request = e.request
    if (!request) return false
    return isAuthenticated // user is authenticated
      && request.toString().startsWith('/api') // is API request
      && !IGNORE_REFRESH_ROUTES.includes(request.toString()) // is not ignored route
      && e.response?.status === 401 // is unauthorized
  },
})

export function $fetchAPI<T = unknown>(url: NitroFetchRequest, options: FetchAPIOptions = {}) {
  const actualMethod = options.method?.toUpperCase() as typeof options.method
  const actualOptions = { retry: 0, ...options, method: actualMethod } satisfies FetchAPIOptions

  const actualUrl = actualOptions.isNotApi === true ? url : `/api${url}`

  return $fetchRefresh<T>(actualUrl, {
    ...actualOptions,
    ...createFetchInterceptors(
      options.onRequest,
      options.onResponse,
      options.onRequestError,
      options.onResponseError,
    ),
  })
}
