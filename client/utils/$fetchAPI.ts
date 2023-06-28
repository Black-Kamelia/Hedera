type FetchOptions = Parameters<typeof $fetch>[1]

export type FetchAPIOptions = FetchOptions & {
  useAPIBaseURL?: boolean
}

export function getActualURL(url: string, useAPIBaseURL?: boolean) {
  return useAPIBaseURL ? useRuntimeConfig().public.apiBaseUrl + url : url
}

export function $fetchAPI(url: string, options: FetchAPIOptions = {}) {
  const actualUrl = getActualURL(url, options.useAPIBaseURL)
  return $fetch(actualUrl, { ...options, retry: 0 })
}
