import type { FetchError } from 'ofetch'

type $Fetch = typeof globalThis.$fetch
type $FetchParams = Parameters<$Fetch>
type $FetchUrl = $FetchParams[0]
type $FetchOptions = $FetchParams[1]
type $FetchReturnType = ReturnType<$Fetch>

export interface Configuration {
  refreshToken: (fetch: $Fetch) => Promise<void>
  shouldRefreshToken: (error: FetchError) => boolean
  fetch: $Fetch
}

export function configureRefreshFetch(config: Configuration): $Fetch {
  const { refreshToken, shouldRefreshToken, fetch } = config

  let refreshingPromise: Promise<void> | null = null

  function wrappedFetch(url: $FetchUrl, options: $FetchOptions): $FetchReturnType {
    if (refreshingPromise !== null) {
      return refreshingPromise
        .then(() => fetch(url, options))
        .catch(() => fetch(url, options)) // fetch anyway to reject with the correct error
    }

    return fetch(url, options).catch((error) => {
      if (!shouldRefreshToken(error)) throw error

      if (refreshingPromise === null) {
        refreshingPromise = new Promise((resolve, reject) => refreshToken(fetch)
          .then(() => {
            refreshingPromise = null
            resolve()
          })
          .catch((refreshTokenError) => {
            refreshingPromise = null
            reject(refreshTokenError)
          }))
      }

      return refreshingPromise
        .catch(() => { throw error })
        .then(() => fetch(url, options))
    })
  }

  wrappedFetch.raw = fetch.raw
  wrappedFetch.create = fetch.create

  return wrappedFetch as $Fetch
}
