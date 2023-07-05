import type { FetchAPIOptions, Interceptor, OnResponseErrorInterceptorFunction } from './types'

interface AuthRefreshCache {
  refreshCall?: Promise<any>
  requestQueueInterceptor?: Interceptor<'onRequest'>
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

function createRequestQueueInterceptor(
  cache: AuthRefreshCache,
  options: FetchAPIOptions,
): Interceptor<'onRequest'> {
  if (!cache.refreshCall)
    throw new Error('Assertion Error: refreshCall must be set')

  if (!cache.requestQueueInterceptor) {
    cache.requestQueueInterceptor = {
      route: skipRefreshRoutes,
      fn({ request, options }) {

      },
    } satisfies Interceptor<'onRequest'>
  }

  return cache.requestQueueInterceptor
}

function unsetCache(cache: AuthRefreshCache) {
  cache.refreshCall = undefined
  cache.requestQueueInterceptor = undefined
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
      .finally(() => unsetCache(cache))
  }
}
