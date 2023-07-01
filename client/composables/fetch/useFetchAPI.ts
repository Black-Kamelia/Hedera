import type { UseFetchOptions } from 'nuxt/app'

export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  ignoreAPIBaseURL?: boolean
}

export function useFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  const actualOptions = { retry: 0, ...options }
  if (options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return useFetch(url, { ...options, retry: 0 })
}

export function useLazyFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  const actualOptions = { retry: 0, ...options }
  if (options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return useLazyFetch(url, { ...options, retry: 0 })
}
