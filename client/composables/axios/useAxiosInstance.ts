import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'

const axiosInstance = ref<AxiosInstance | null>(null)
export function useAxiosInstance() {
  const appConfig = useRuntimeConfig()
  const axiosMiddlewares = useAxiosMiddlewares()

  if (!axiosInstance.value) {
    axiosInstance.value = axios.create({
      baseURL: appConfig.public.apiBaseUrl,
    })

    axiosMiddlewares.requestMiddlewares.forEach(createInterceptorFactory(
      axiosInstance.value.interceptors.request,
      (config: InternalAxiosRequestConfig<any>) => config.url ?? '',
      () => '', // Well, as of now, errors on request interceptor can never happen, so this is ignorable
    ))

    axiosMiddlewares.responseMiddlewares.forEach(createInterceptorFactory(
      axiosInstance.value.interceptors.response,
      (response: AxiosResponse<any, any>) => response.config.url ?? '',
      (error: AxiosError) => error.response?.config.url ?? '',
    ))
  }

  return axiosInstance as Ref<AxiosInstance>
}
