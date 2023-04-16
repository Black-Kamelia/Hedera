import type { AxiosMiddlewares } from './types'

export const skipRefreshRoutes = ['/refresh', '/login', '/users/signup', '/upload/token']

export function useAxiosMiddlewares(): ComputedRef<AxiosMiddlewares> {
  const toast = useToast()

  return computed(() => ({
    requestMiddlewares: [
      {
        route: skipRefreshRoutes,
        negateRoute: true,
        onFulfilled: (config) => {
          const tokens = getTokensFromLocalStorage()
          if (tokens) {
            config.headers.Authorization = `Bearer ${tokens.accessToken}`
            config.headers['Access-Control-Allow-Origin'] = '*'
          }

          return config
        },
      },
      {
        route: '/refresh',
        onFulfilled: (config) => {
          const tokens = getTokensFromLocalStorage()
          if (tokens) {
            config.headers.Authorization = `Bearer ${tokens.refreshToken}`
            config.headers['Access-Control-Allow-Origin'] = '*'
          }

          return config
        },
      },
    ],
    responseMiddlewares: [
      {
        route: '/refresh',
        onRejected: (error) => {
          if (error.response?.status !== 401)
            return Promise.reject(error)

          const { setTokens } = useAuth()
          setTokens(null)
          clearTokensFromLocalStorage()
          toast.add({
            severity: 'error',
            summary: 'Session',
            detail: 'Session expired, please login again',
            life: 3000,
            closable: true,
          })
          navigateTo('/login')

          return Promise.resolve()
        },
      },
    ],
  }))
}
