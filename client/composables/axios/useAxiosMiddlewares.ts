import type { AxiosMiddlewares } from './types'

export const skipRefreshRoutes = ['/refresh', '/login', '/users/signup', '/upload/token']

export function useAxiosMiddlewares(): ComputedRef<AxiosMiddlewares> {
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
    ],
  }))
}
