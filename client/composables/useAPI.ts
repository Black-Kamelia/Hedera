import type { StrictUseAxiosReturn, UseAxiosOptions } from '@vueuse/integrations/useAxios'
import { useAxios } from '@vueuse/integrations/useAxios'
import _axios from 'axios'
import type { AxiosResponse, InternalAxiosRequestConfig, RawAxiosRequestConfig } from 'axios'

const appConfig = useRuntimeConfig()
const axios = _axios.create({
  baseURL: appConfig.public.apiBaseUrl,
})

axiosMiddlewares.requestMiddlewares.forEach(createInterceptorFactory(
  axios.interceptors.request,
  (config: InternalAxiosRequestConfig<any>) => config.url ?? '',
))

axiosMiddlewares.responseMiddlewares.forEach(createInterceptorFactory(
  axios.interceptors.response,
  (response: AxiosResponse<any, any>) => response.config.url ?? '',
))

export default function useAPI<T = any, R = AxiosResponse<T>, D = any>(
  url: string,
  config?: RawAxiosRequestConfig<D>,
  options?: UseAxiosOptions<T>,
): StrictUseAxiosReturn<T, R, D> & PromiseLike<StrictUseAxiosReturn<T, R, D>> {
  return useAxios(url, config ?? {}, axios, options ?? {})
}
