import type { FetchAPIOptions } from './types'

export function $fetchAPI<T = unknown>(url: string, options: FetchAPIOptions = {}) {
  const actualOptions = { retry: 0, ...options }
  if (options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return $fetch<T>(url, {
    ...actualOptions,
    onRequest(context) {
      actualOptions.onRequest?.(context)
    },
    onResponse(context) {
      actualOptions.onResponse?.(context)
    },
    onRequestError(context) {
      actualOptions.onRequestError?.(context)
    },
    onResponseError(context) {
      actualOptions.onResponseError?.(context)
    },
  })
}
