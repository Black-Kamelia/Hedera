import type { AxiosMiddlewares } from './types'
import type { Tokens } from '~/stores/useAuth'

function getTokensFromLocalStorage(): Tokens | null {
  const auth = localStorage.getItem('auth')
  if (!auth)
    return null

  const parsed = JSON.parse(auth)
  if (!parsed.tokens || !parsed.tokens.accessToken || !parsed.tokens.refreshToken)
    return null

  return parsed.tokens
}

export function useAxiosMiddlewares(): ComputedRef<AxiosMiddlewares> {
  // const toast = useToast()

  return computed(() => ({
    requestMiddlewares: [
      {
        route: '/refresh',
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
        route: /^\/(refresh|login|users\/signup|upload\/token)/,
        negateRoute: true,
        onRejected: (error) => {
          if (error.response?.status === 401) {
            const { refresh } = useAuth()
            return refresh()
              .then((refreshed) => {
                if (refreshed) {
                  const axiosInstance = useAxiosInstance()
                  error.config!.headers.Authorization = `Bearer ${getTokensFromLocalStorage()?.accessToken}`
                  return axiosInstance.value(error.config!)
                }
                else {
                  // toast.add({
                  //   severity: 'error',
                  //   summary: 'Session',
                  //   detail: 'Session expired, please login again',
                  //   life: 5000,
                  // })
                  navigateTo('/login')
                }
              })
          }

          return Promise.reject(error)
        },
      },
    ],
  }))
}
