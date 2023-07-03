import type { UseFetchOptions } from 'nuxt/app'

type FetchOptions = Parameters<typeof $fetch>[1]
export type FetchAPIOptions = FetchOptions & {
  ignoreAPIBaseURL?: boolean
}

export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  ignoreAPIBaseURL?: boolean
}

export type OnRequestInterceptorFunction<T = any> = FetchAPIOptions['onRequest'] & UseFetchAPIOptions<T>['onRequest']
export type OnResponseInterceptorFunction<T = any> = FetchAPIOptions['onResponse'] & UseFetchAPIOptions<T>['onResponse']
export type OnRequestErrorInterceptorFunction<T = any> = FetchAPIOptions['onRequestError'] & UseFetchAPIOptions<T>['onRequestError']
export type OnResponseErrorInterceptorFunction<T = any> = FetchAPIOptions['onResponseError'] & UseFetchAPIOptions<T>['onResponseError']

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
  negateRoute?: boolean
  fn: InterceptorFunction<I, T>
}

export function defineInterceptor<I extends InterceptorType, T = any>(interceptor: Interceptor<I, T>) {
  return interceptor
}

export function checkRoute(url: string, route?: InterceptorRoute): boolean {
  if (!route)
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
