import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import createAuthRefreshInterceptor from 'axios-auth-refresh'
import type { Tokens } from '~/stores/useAuth'

function tryRefreshFactory(axios: AxiosInstance) {
  return function useTryRefresh(failedRequest: AxiosError) {
    if (skipRefreshRoutes.includes(failedRequest.response?.config.url ?? ''))
      return Promise.reject(failedRequest)

    const { setTokens } = useAuth()

    return axios.post('/refresh').then((tokenRefreshResponse) => {
      const tokens = tokenRefreshResponse.data as Tokens
      failedRequest.response!.config.headers.Authorization = `Bearer ${tokens?.accessToken}`
      setTokens(tokens)
      return Promise.resolve()
    })
  }
}

export function useAxiosInstance() {
  const appConfig = useRuntimeConfig()
  const axiosMiddlewares = useAxiosMiddlewares()
  const toast = useToast()

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

    createAuthRefreshInterceptor(it, tryRefreshFactory(it))

    return it
  })

  return axiosInstance
}
