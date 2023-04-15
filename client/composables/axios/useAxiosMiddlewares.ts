import type { AxiosMiddlewares } from './types'

export function useAxiosMiddlewares(): AxiosMiddlewares {
  const toast = useToast()
  const { isAuthed, tokens, refresh } = useAuth()

  return {
    requestMiddlewares: [
      {
        route: '/refresh',
        negateRoute: true,
        onFulfilled: (config) => {
          if (isAuthed.value)
            config.headers.Authorization = `Bearer ${tokens.value!.accessToken}`

          return config
        },
      },
      {
        route: '/refresh',
        onFulfilled: (config) => {
          if (isAuthed.value)
            config.headers.Authorization = `Bearer ${tokens.value!.refreshToken}`

          return config
        },
      },
    ],
    responseMiddlewares: [
      {
        route: '/refresh',
        negateRoute: true,
        onRejected: (error) => {
          if (error.response?.status === 401) {
            refresh()
              .then(() => {
                const axiosInstance = useAxiosInstance()
                return axiosInstance.value.request(error.config!)
              })
              .catch((error) => {
                toast.add({
                  severity: 'error',
                  summary: 'Session',
                  detail: error.message,
                })
              })
            return null
          }

          return Promise.reject(error)
        },
      },
    ],
  }
}
