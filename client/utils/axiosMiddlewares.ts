export interface AxiosMiddleware {
  route: string | RegExp
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

interface AxiosMiddlewares {
  requestMiddlewares: AxiosRequestMiddleware<any>[]
  responseMiddlewares: AxiosResponseMiddleware<any, any>[]
}
