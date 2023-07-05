import type { UseFetchOptions } from 'nuxt/app'

type _FetchOptions = Parameters<typeof $fetch>[1]
export type FetchAPIOptions = _FetchOptions & {
  ignoreAPIBaseURL?: boolean
  skipAuthRefresh?: boolean
}
export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  ignoreAPIBaseURL?: boolean
  skipAuthRefresh?: boolean
}

export type OnRequestInterceptorFunction<T = any> = FetchAPIOptions['onRequest'] & NonNullable<UseFetchAPIOptions<T>['onRequest']>
export type OnResponseInterceptorFunction<T = any> = FetchAPIOptions['onResponse'] & NonNullable<UseFetchAPIOptions<T>['onResponse']>
export type OnRequestErrorInterceptorFunction<T = any> = FetchAPIOptions['onRequestError'] & NonNullable<UseFetchAPIOptions<T>['onRequestError']>
export type OnResponseErrorInterceptorFunction<T = any> = FetchAPIOptions['onResponseError'] & NonNullable<UseFetchAPIOptions<T>['onResponseError']>

export type InterceptorType = 'onRequest' | 'onResponse' | 'onRequestError' | 'onResponseError'

interface InterceptorLookup<T = any> {
  onRequest: OnRequestInterceptorFunction<T>
  onResponse: OnResponseInterceptorFunction<T>
  onRequestError: OnRequestErrorInterceptorFunction<T>
  onResponseError: OnResponseErrorInterceptorFunction<T>
}
export type InterceptorFunction<I extends InterceptorType, T = any> = InterceptorLookup<T>[I]

export type InterceptorRoute = string | string[] | RegExp | ((url: string) => boolean)

export interface Interceptor<I extends InterceptorType, T = any> {
  route?: InterceptorRoute
  withBaseURL?: boolean
  negateRoute?: boolean
  fn: InterceptorFunction<I, T>
}

export interface FetchInterceptors {
  onRequest: OnRequestErrorInterceptorFunction
  onResponse: OnResponseInterceptorFunction
  onRequestError: OnRequestErrorInterceptorFunction
  onResponseError: OnResponseErrorInterceptorFunction
}
