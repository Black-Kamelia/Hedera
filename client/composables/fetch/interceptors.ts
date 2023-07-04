export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: ['/refresh', '/login', '/users/signup', '/upload/token'],
    negateRoute: true,
    fn({ options }) {
      const auth = useAuth()
      const tokens = auth.tokens.value
      if (tokens) {
        options.headers = {
          ...options.headers,
          'Authorization': `Bearer ${tokens.accessToken}`,
          'Access-Control-Allow-Origin': '*',
        }
      }
    },
  },

  {
    route: '/refresh',
    fn({ options }) {
      const auth = useAuth()
      const tokens = auth.tokens.value
      if (tokens) {
        options.headers = {
          ...options.headers,
          'Authorization': `Bearer ${tokens.refreshToken}`,
          'Access-Control-Allow-Origin': '*',
        }
      }
    },
  },
])

export const onResponseInterceptors = defineInterceptors<'onResponse'>([])

export const onRequestErrorInterceptors = defineInterceptors<'onRequestError'>([])

export const onResponseErrorInterceptors = defineInterceptors<'onResponseError'>([
  {
    route: '/refresh',
    fn({ error, response }) {
      if (!error || response?.status !== 401)
        return Promise.reject(error)

      const auth = useAuth()
      auth.setTokens(null)
      auth.setUser(null)
      useEventBus(RefreshTokenExpiredEvent).emit({ error })

      return Promise.resolve()
    },
  },
])
