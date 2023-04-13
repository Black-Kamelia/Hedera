import type { AxiosInterceptorOptions, AxiosResponse, InternalAxiosRequestConfig } from 'axios'

export interface AxiosMiddleware {
  route: string | RegExp | ((url: string) => boolean)
}

export interface AxiosRequestMiddleware<D> extends AxiosMiddleware {
  onFulfilled?: ((value: InternalAxiosRequestConfig<D>) => InternalAxiosRequestConfig<D> | Promise<InternalAxiosRequestConfig<D>>) | null
  onRejected?: ((error: any) => any) | null
  options?: AxiosInterceptorOptions
}

export interface AxiosResponseMiddleware<T, D> extends AxiosMiddleware {
  onFulfilled?: ((value: AxiosResponse<T, D>) => AxiosResponse<T, D> | Promise<AxiosResponse<T, D>>) | null
  onRejected?: ((error: any) => any) | null
  options?: AxiosInterceptorOptions
}

export interface AxiosMiddlewares {
  requestMiddlewares: AxiosRequestMiddleware<any>[]
  responseMiddlewares: AxiosResponseMiddleware<any, any>[]
}
