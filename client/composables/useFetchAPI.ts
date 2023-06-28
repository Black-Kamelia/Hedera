import type { UseFetchOptions } from 'nuxt/app'

export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  useAPIBaseURL?: boolean
}

export function useFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  const actualUrl = getActualURL(url, options.useAPIBaseURL)
  return useFetch(actualUrl, { ...options, retry: 0 })
}

export function useLazyFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  const actualUrl = getActualURL(url, options.useAPIBaseURL)
  return useLazyFetch(actualUrl, { ...options, retry: 0 })
}
