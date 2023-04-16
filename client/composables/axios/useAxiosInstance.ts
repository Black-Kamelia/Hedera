import type { AxiosError, AxiosResponse, InternalAxiosRequestConfig } from 'axios'
import axios from 'axios'
import createAuthRefreshInterceptor from 'axios-auth-refresh'
import type { ToastServiceMethods } from 'primevue/toastservice'

function tryRefresh(toast: ToastServiceMethods) {
  return async function (failedRequest: AxiosError) {
    if (skipRefreshRoutes.includes(failedRequest.response?.config.url ?? ''))
      return Promise.reject(failedRequest)

    const { refresh } = useAuth()
    const refreshed = await refresh()

    if (!refreshed) {
      clearTokensFromLocalStorage()
      toast.add({
        severity: 'error',
        summary: 'Session',
        detail: 'Session expired, please login again',
        life: 5000,
      })
      navigateTo('/login')
      return Promise.reject(failedRequest)
    }

    const tokens = getTokensFromLocalStorage()
    failedRequest.response!.config.headers.Authorization = `Bearer ${tokens?.accessToken}`
    return Promise.resolve()
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

    createAuthRefreshInterceptor(it, tryRefresh(toast))

    return it
  })

  return axiosInstance
}
