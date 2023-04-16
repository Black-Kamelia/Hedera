import type { StrictUseAxiosReturn, UseAxiosOptions } from '@vueuse/integrations/useAxios'
import { useAxios } from '@vueuse/integrations/useAxios'
import type { AxiosResponse, RawAxiosRequestConfig } from 'axios'

export default function useAPI<T = any, R = AxiosResponse<T>, D = any>(
  url: string,
  config?: RawAxiosRequestConfig<D> & { skipAuthRefresh?: boolean },
  options?: UseAxiosOptions<T>,
): StrictUseAxiosReturn<T, R, D> & PromiseLike<StrictUseAxiosReturn<T, R, D>> {
  const axiosInstance = useAxiosInstance()
  if (options)
    return useAxios(url, config ?? {}, axiosInstance.value, options)
  else
    return useAxios(url, config ?? {}, axiosInstance.value)
}
