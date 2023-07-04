import type { UseFetchOptions } from 'nuxt/app'

type FetchOptions = Parameters<typeof $fetch>[1]
export type FetchAPIOptions = FetchOptions & {
  ignoreAPIBaseURL?: boolean
}

export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  ignoreAPIBaseURL?: boolean
}

export type OnRequestInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onRequest'] & UseFetchAPIOptions<T>['onRequest']>
export type OnResponseInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onResponse'] & UseFetchAPIOptions<T>['onResponse']>
export type OnRequestErrorInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onRequestError'] & UseFetchAPIOptions<T>['onRequestError']>
export type OnResponseErrorInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onResponseError'] & UseFetchAPIOptions<T>['onResponseError']>

export type InterceptorType = 'onRequest' | 'onResponse' | 'onRequestError' | 'onResponseError'

export type InterceptorFunction<I extends InterceptorType, T = any> =
  I extends 'onRequest' ? OnRequestInterceptorFunction<T> :
    I extends 'onResponse' ? OnResponseInterceptorFunction<T> :
      I extends 'onRequestError' ? OnRequestErrorInterceptorFunction<T> :
        I extends 'onResponseError' ? OnResponseErrorInterceptorFunction<T> :
          never

export type InterceptorRoute = string | string[] | RegExp | ((url: string) => boolean)

export interface Interceptor<I extends InterceptorType, T = any> {
  route?: InterceptorRoute
  withBaseURL?: boolean
  negateRoute?: boolean
  fn: InterceptorFunction<I, T>
}

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

export interface FetchInterceptors {
  onRequest: OnRequestErrorInterceptorFunction
  onResponse: OnResponseInterceptorFunction
  onRequestError: OnRequestErrorInterceptorFunction
  onResponseError: OnResponseErrorInterceptorFunction
}

function tryForInterceptors<I extends InterceptorType>(
  interceptors: Interceptor<I>[],
) {
  return function (context: any) {
    const url = context.request.toString()
    for (const { route, withBaseURL = false, negateRoute = false, fn } of interceptors) {
      const trimmedURL = withBaseURL ? url : url.replace(context.options.baseURL ?? '', '')
      if (checkRoute(trimmedURL, route) !== negateRoute)
        fn(context)
    }
  }
}

export function createFetchInterceptors(): FetchInterceptors {
  return {
    onRequest: tryForInterceptors(onRequestInterceptors),
    onResponse: tryForInterceptors(onResponseInterceptors),
    onRequestError: tryForInterceptors(onRequestErrorInterceptors),
    onResponseError: tryForInterceptors(onResponseErrorInterceptors),
  }
}
