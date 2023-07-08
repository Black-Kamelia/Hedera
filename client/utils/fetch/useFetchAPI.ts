import type { OnRequestErrorInterceptorFunction, OnRequestInterceptorFunction, OnResponseErrorInterceptorFunction, OnResponseInterceptorFunction, UseFetchAPIOptions } from './types'

function useFetch_<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
  useFetchComposable: typeof useFetch | typeof useLazyFetch,
) {
  const actualOptions = { retry: 0, ...options }
  if (options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return useFetchComposable(url, {
    ...actualOptions,
    ...createFetchInterceptors(
      options.onRequest as OnRequestInterceptorFunction<T>,
      options.onResponse as OnResponseInterceptorFunction<T>,
      options.onRequestError as OnRequestErrorInterceptorFunction<T>,
      options.onResponseError as OnResponseErrorInterceptorFunction<T>,
    ),
  })
}

export function useFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  return useFetch_(url, options, useFetch)
}

export function useLazyFetchAPI<T = any>(
  url: string,
  options: UseFetchAPIOptions<T> = {},
) {
  return useFetch_(url, options, useLazyFetch)
}
