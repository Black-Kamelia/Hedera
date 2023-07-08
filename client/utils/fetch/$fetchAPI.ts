import { $fetch } from 'ofetch'
import type { FetchAPIOptions } from './types'
import type { Tokens } from '~/stores/useAuth'

const $fetchRefresh = configureRefreshFetch({
  fetch: $fetch,
  refreshToken(fetch) {
    const apiUrl = useRuntimeConfig().public.apiBaseUrl
    const accessTokenExpiredEvent = useEventBus(AccessTokenExpiredEvent)
    const tokensRefreshedEvent = useEventBus(TokensRefreshedEvent)
    const refreshTokenExpiredEvent = useEventBus(RefreshTokenExpiredEvent)

    accessTokenExpiredEvent.emit()

    const auth = useAuth()

    return fetch<Tokens>('/refresh', {
      baseURL: apiUrl,
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${auth.tokens?.refreshToken}`,
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
    const apiUrl = useRuntimeConfig().public.apiBaseUrl
    return e.request?.startsWith(apiUrl) && e.response?.status === 401
  },
})

export function $fetchAPI<T = unknown>(url: string, options: FetchAPIOptions = {}) {
  const actualOptions = { retry: 0, ...options }
  if (!options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return $fetchRefresh<T>(url, {
    ...actualOptions,
    ...createFetchInterceptors(
      options.onRequest,
      options.onResponse,
      options.onRequestError,
      options.onResponseError,
    ),
  })
}
