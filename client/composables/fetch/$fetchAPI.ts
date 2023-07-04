import type { FetchAPIOptions } from './types'

export function $fetchAPI<T = unknown>(url: string, options: FetchAPIOptions = {}) {
  const actualOptions = { retry: 0, ...options }
  if (!options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return $fetch<T>(url, {
    ...actualOptions,
    ...createFetchInterceptors(
      options.onRequest,
      options.onResponse,
      options.onRequestError,
      options.onResponseError,
    ),
  })
}
