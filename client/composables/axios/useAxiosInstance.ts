import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'

export function useAxiosInstance() {
  const appConfig = useRuntimeConfig()
  const axiosMiddlewares = useAxiosMiddlewares()

  const axiosInstance = computed(() => {
    const it = axios.create({
      baseURL: appConfig.public.apiBaseUrl,
    })

    axiosMiddlewares.value.requestMiddlewares.forEach(createInterceptorFactory(
      it.interceptors.request,
      (config: InternalAxiosRequestConfig<any>) => config.url ?? '',
      (error: AxiosError) => error.config?.url ?? '',
    ))

    axiosMiddlewares.value.responseMiddlewares.forEach(createInterceptorFactory(
      it.interceptors.response,
      (response: AxiosResponse<any, any>) => response.config.url ?? '',
      (error: AxiosError) => error.response?.config.url ?? '',
    ))

    return it
  })

  return axiosInstance
}
