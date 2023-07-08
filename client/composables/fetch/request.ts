type $FetchParams = Parameters<typeof $fetch>
type $FetchUrl = $FetchParams[0]
type $FetchOptions = $FetchParams[1]
type $FetchReturnType = ReturnType<typeof $fetch>

export interface Configuration {
  refreshToken: (fetch: typeof $fetch) => Promise<void>
  shouldRefreshToken: (error: any) => boolean
  fetch: typeof $fetch
}

export function configureRefreshFetch(config: Configuration): typeof $fetch {
  const { refreshToken, shouldRefreshToken, fetch } = config

  let refreshingPromise: Promise<void> | null = null

  function wrappedFetch(url: $FetchUrl, options: $FetchOptions): $FetchReturnType {
    if (refreshingPromise !== null) {
      return refreshingPromise
        .then(() => fetch(url, options))
        .catch(() => fetch(url, options)) // fetch anyway to reject with the correct error
    }

    return fetch(url, options).catch((error) => {
      if (!shouldRefreshToken(error))
        throw error

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

  wrappedFetch.prototype.raw = fetch.raw
  wrappedFetch.prototype.create = fetch.create

  return wrappedFetch as typeof $fetch
}
