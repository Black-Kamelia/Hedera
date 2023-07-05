import type { FetchAPIOptions, OnResponseErrorInterceptorFunction } from './types'

interface AuthRefreshCache {
  refreshCall?: Promise<any>
  requestQueueInterceptorId?: number
}

function shouldInterceptError(options: FetchAPIOptions, errorResponse: FetchResponseError) {
  if (!errorResponse)
    return false

  if (options.skipAuthRefresh)
    return false

  if (errorResponse.status !== 401 && errorResponse.status !== 0)
    return false

  return true
}

function createRefreshCall(
  refreshAuthCall: (error: FetchResponseError) => Promise<any>,
  error: FetchResponseError,
  cache: AuthRefreshCache,
): Promise<any> {
  if (!cache.refreshCall) {
    cache.refreshCall = refreshAuthCall(error)
    if (typeof cache.refreshCall.then !== 'function')
      return Promise.reject(new Error('refreshAuthCall did not return a promise'))
  }
  return cache.refreshCall
}

type FetchResponseError = Parameters<OnResponseErrorInterceptorFunction>[0]['response']
export function createAuthRefreshInterceptorFunction(refreshAuthCall: (error: FetchResponseError) => Promise<any>): OnResponseErrorInterceptorFunction {
  if (typeof refreshAuthCall !== 'function')
    throw new Error('refreshAuthCall must be a function')

  const cache: AuthRefreshCache = {}

  return function ({ response: error, options }) {
    if (!shouldInterceptError(options as FetchAPIOptions, error))
      return Promise.reject(error)

    const refreshing = createRefreshCall(refreshAuthCall, error, cache)

    // TODO: create queue

    return refreshing
      .catch(refreshError => Promise.reject(refreshError))
      .then(() => { /* TODO: resend request */ })
      .finally(() => { /* TODO: unset cache */ })
  }
}
