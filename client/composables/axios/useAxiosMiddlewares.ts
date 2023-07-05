import type { AxiosMiddlewares } from './types'

export const skipRefreshRoutes = ['/refresh', '/login', '/users/signup', '/upload/token']

export function useAxiosMiddlewares(): ComputedRef<AxiosMiddlewares> {
  const refreshTokenExpiredEvent = useEventBus(RefreshTokenExpiredEvent)

  return computed(() => ({
    requestMiddlewares: [
      {
        route: skipRefreshRoutes,
        negateRoute: true,
        onFulfilled: (config) => {
          const { tokens } = storeToRefs(useAuth())
          if (tokens.value) {
            config.headers.Authorization = `Bearer ${tokens.value.accessToken}`
            config.headers['Access-Control-Allow-Origin'] = '*'
          }

          return config
        },
      },
      {
        route: '/refresh',
        onFulfilled: (config) => {
          const { tokens } = storeToRefs(useAuth())
          if (tokens.value) {
            config.headers.Authorization = `Bearer ${tokens.value.refreshToken}`
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

          const { setTokens, setUser } = useAuth()
          setTokens(null)
          setUser(null)
          refreshTokenExpiredEvent.emit({ error })

          return Promise.resolve()
        },
      },
    ],
  }))
}
