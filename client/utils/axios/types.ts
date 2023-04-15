import type { AxiosInterceptorManager, AxiosInterceptorOptions, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

export interface AxiosMiddleware<V> {
  route: string | RegExp | ((url: string) => boolean)
  onFulfilled?: ((value: V) => V | Promise<V>) | null
  onRejected?: ((error: any) => any) | null
  options?: AxiosInterceptorOptions
}

export type AxiosRequestMiddleware<D> = AxiosMiddleware<InternalAxiosRequestConfig<D>>

export type AxiosResponseMiddleware<T, D> = AxiosMiddleware<AxiosResponse<T, D>>

export interface AxiosMiddlewares {
  requestMiddlewares: AxiosRequestMiddleware<any>[]
  responseMiddlewares: AxiosResponseMiddleware<any, any>[]
}

function checkRoute(route: string | RegExp | ((url: string) => boolean), url: string): boolean {
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
  routerMapper: (input: V) => string,
): (middleware: T) => void {
  return function (middleware: T) {
    let passes = false

    axiosInterceptorManager.use(
      (input) => {
        if (checkRoute(middleware.route, routerMapper(input))) {
          passes = true
          middleware.onFulfilled?.(input)
        }

        return input
      },
      (error) => {
        console.log('error', error)

        if (passes)
          middleware.onRejected?.(error)

        return Promise.reject(error)
      },
      middleware.options,
    )
  }
}
