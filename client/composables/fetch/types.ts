import type { UseFetchOptions } from 'nuxt/app'

type FetchOptions = Parameters<typeof $fetch>[1]
export type FetchAPIOptions = FetchOptions & {
  ignoreAPIBaseURL?: boolean
}

export type UseFetchAPIOptions<T> = UseFetchOptions<T> & {
  ignoreAPIBaseURL?: boolean
}

export type OnRequestInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onRequest']> & NonNullable<UseFetchAPIOptions<T>['onRequest']>
export type OnResponseInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onResponse']> & NonNullable<UseFetchAPIOptions<T>['onResponse']>
export type OnRequestErrorInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onRequestError']> & NonNullable<UseFetchAPIOptions<T>['onRequestError']>
export type OnResponseErrorInterceptorFunction<T = any> = NonNullable<FetchAPIOptions['onResponseError']> & NonNullable<UseFetchAPIOptions<T>['onResponseError']>

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

export interface FetchInterceptors {
  onRequest: OnRequestErrorInterceptorFunction
  onResponse: OnResponseInterceptorFunction
  onRequestError: OnRequestErrorInterceptorFunction
  onResponseError: OnResponseErrorInterceptorFunction
}
