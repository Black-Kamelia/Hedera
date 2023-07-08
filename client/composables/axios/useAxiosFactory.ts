import type { AxiosError, AxiosInstance, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import createAuthRefreshInterceptor from 'axios-auth-refresh'
import type { Tokens } from '~/stores/useAuth'

function tryRefreshFactory(axios: AxiosInstance) {
  return async function useTryRefresh(failedRequest: AxiosError) {
    const accessTokenExpiredEvent = useEventBus(AccessTokenExpiredEvent)
    const tokensRefreshedEvent = useEventBus(TokensRefreshedEvent)

    accessTokenExpiredEvent.emit()

    if (skipRefreshRoutes.includes(failedRequest.response?.config.url ?? ''))
      return Promise.reject(failedRequest)

    const { setTokens } = useAuth()

    const tokenRefreshResponse = await axios.post('/refresh')
    const tokens = tokenRefreshResponse.data as Tokens
    failedRequest.response!.config.headers.Authorization = `Bearer ${tokens?.accessToken}`
    setTokens(tokens)
    tokensRefreshedEvent.emit({ tokens })
    return await Promise.resolve()
  }
}

export function useAxiosFactory() {
  const appConfig = useRuntimeConfig()
  const axiosMiddlewares = useAxiosMiddlewares()

  return () => {
    const axiosInstance = axios.create({
      baseURL: appConfig.public.apiBaseUrl,
    })

    axiosMiddlewares.value.requestMiddlewares.forEach(createInterceptorFactory(
      axiosInstance.interceptors.request,
      (config: InternalAxiosRequestConfig<any>) => config.url ?? '',
      (error: AxiosError) => error.config?.url ?? '',
    ))

    axiosMiddlewares.value.responseMiddlewares.forEach(createInterceptorFactory(
      axiosInstance.interceptors.response,
      (response: AxiosResponse<any, any>) => response.config.url ?? '',
      (error: AxiosError) => error.response?.config.url ?? '',
    ))

    createAuthRefreshInterceptor(axiosInstance, tryRefreshFactory(axiosInstance))

    return axiosInstance
  }
}
