type FetchOptions = Parameters<typeof $fetch>[1]

export type FetchAPIOptions = FetchOptions & {
  ignoreAPIBaseURL?: boolean
}

export function $fetchAPI(url: string, options: FetchAPIOptions = {}) {
  const actualOptions = { retry: 0, ...options }
  if (options.ignoreAPIBaseURL)
    actualOptions.baseURL = useRuntimeConfig().public.apiBaseUrl

  return $fetch(url, actualOptions)
}
