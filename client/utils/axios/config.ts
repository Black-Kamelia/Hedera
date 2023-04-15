import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'

const appConfig = useRuntimeConfig()
export const axiosInstance = axios.create({
  baseURL: appConfig.public.apiBaseUrl,
})

axiosMiddlewares.requestMiddlewares.forEach(createInterceptorFactory(
  axiosInstance.interceptors.request,
  (config: InternalAxiosRequestConfig<any>) => config.url ?? '',
  () => '', // Well, as of now, errors on request interceptor can never happen, so this is ignorable
))

axiosMiddlewares.responseMiddlewares.forEach(createInterceptorFactory(
  axiosInstance.interceptors.response,
  (response: AxiosResponse<any, any>) => response.config.url ?? '',
  (error: AxiosError) => error.response?.config.url ?? '',
))
