import type { AxiosError, AxiosInterceptorManager, AxiosInterceptorOptions, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

export interface AxiosMiddleware<V> {
  route?: string | RegExp | ((url: string) => boolean) | null
  onFulfilled?: ((value: V) => V | Promise<V>) | null
  onRejected?: ((error: AxiosError) => any) | null
  options?: AxiosInterceptorOptions
}

export type AxiosRequestMiddleware<D> = AxiosMiddleware<InternalAxiosRequestConfig<D>>

export type AxiosResponseMiddleware<T, D> = AxiosMiddleware<AxiosResponse<T, D>>

export interface AxiosMiddlewares {
  requestMiddlewares: AxiosRequestMiddleware<any>[]
  responseMiddlewares: AxiosResponseMiddleware<any, any>[]
}

function checkRoute(url: string, route?: string | RegExp | ((url: string) => boolean) | null): boolean {
  if (!route)
    return true

  if (typeof route === 'string')
    return route === url

  if (route instanceof RegExp)
    return route.test(url)

  if (typeof route === 'function')
    return route(url)

  return false
}

export function createInterceptorFactory<V, T extends AxiosMiddleware<V>>(
  axiosInterceptorManager: AxiosInterceptorManager<V>,
  routeMapper: (input: V) => string,
  errorRouteMapper: (error: AxiosError) => string,
): (middleware: T) => void {
  return function (middleware: T) {
    axiosInterceptorManager.use(
      (input) => {
        if (checkRoute(routeMapper(input), middleware.route))
          middleware.onFulfilled?.(input)

        return input
      },
      (error: AxiosError) => {
        if (checkRoute(errorRouteMapper(error), middleware.route))
          middleware.onRejected?.(error)

        return Promise.reject(error)
      },
      middleware.options,
    )
  }
}
