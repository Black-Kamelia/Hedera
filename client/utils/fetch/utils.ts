import type { FetchInterceptors, Interceptor, InterceptorFunction, InterceptorRoute, InterceptorType, OnRequestErrorInterceptorFunction, OnRequestInterceptorFunction, OnResponseErrorInterceptorFunction, OnResponseInterceptorFunction } from './types'

export function defineInterceptors<I extends InterceptorType>(interceptors: Interceptor<I>[]) {
  return interceptors
}

export function checkRoute(url: string, route?: InterceptorRoute): boolean {
  if (route === undefined)
    return true

  if (typeof route === 'string')
    return route === url

  if (Array.isArray(route))
    return route.includes(url)

  if (route instanceof RegExp)
    return route.test(url)

  if (typeof route === 'function')
    return route(url)

  return false
}

function tryForInterceptors<I extends InterceptorType>(
  interceptors: Interceptor<I>[],
  optionalInterceptor?: InterceptorFunction<I>,
) {
  return function (context: any) {
    optionalInterceptor?.(context)

    const url = context.request.toString()
    for (const { route, withBaseURL = false, negateRoute = false, fn } of interceptors) {
      const trimmedURL = withBaseURL ? url : url.replace(context.options.baseURL ?? '', '')
      if (checkRoute(trimmedURL, route) !== negateRoute)
        fn(context)
    }
  }
}

export function createFetchInterceptors(
  onRequestInterceptor?: OnRequestInterceptorFunction,
  onResponseInterceptor?: OnResponseInterceptorFunction,
  onRequestErrorInterceptor?: OnRequestErrorInterceptorFunction,
  onResponseErrorInterceptor?: OnResponseErrorInterceptorFunction,
): FetchInterceptors {
  return {
    onRequest: tryForInterceptors(onRequestInterceptors, onRequestInterceptor),
    onResponse: tryForInterceptors(onResponseInterceptors, onResponseInterceptor),
    onRequestError: tryForInterceptors(onRequestErrorInterceptors, onRequestErrorInterceptor),
    onResponseError: tryForInterceptors(onResponseErrorInterceptors, onResponseErrorInterceptor),
  }
}
