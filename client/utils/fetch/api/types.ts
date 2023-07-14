import type { AvailableRouterMethod, NitroFetchOptions, NitroFetchRequest } from 'nitropack'
import type { AsyncDataOptions } from 'nuxt/app'
import type { WatchSource } from 'vue'

type _FetchOptions = Parameters<typeof globalThis.$fetch>[1]
export type FetchAPIOptions = _FetchOptions & {
  ignoreAPIBaseURL?: boolean
  skipAuthRefresh?: boolean
}

type ComputedOptions<T extends Record<string, any>> = {
  [K in keyof T]: T[K] extends Function ? T[K] : T[K] extends Record<string, any> ? ComputedOptions<T[K]> | Ref<T[K]> | T[K] : Ref<T[K]> | T[K]
}

type ComputedFetchOptions<R extends NitroFetchRequest, M extends AvailableRouterMethod<R>> = ComputedOptions<NitroFetchOptions<R, M>>

export interface UseFetchAPIOptions<
  ResT,
  DataT = ResT,
  PickKeys extends KeysOf<DataT> = KeysOf<DataT>,
  DefaultT = null,
  R extends NitroFetchRequest = string & {},
  M extends AvailableRouterMethod<R> = AvailableRouterMethod<R>,
> extends Omit<AsyncDataOptions<ResT, DataT, PickKeys, DefaultT>, 'watch'>, ComputedFetchOptions<R, M> {
  key?: string
  $fetch?: typeof globalThis.$fetch
  watch?: (WatchSource<unknown> | object)[] | false
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
