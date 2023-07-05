export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: ['/refresh', '/login', '/users/signup', '/upload/token'],
    negateRoute: true,
    fn({ options }) {
      const { tokens } = storeToRefs(useAuth())
      if (tokens.value) {
        options.headers = {
          ...options.headers,
          'Authorization': `Bearer ${tokens.value.accessToken}`,
          'Access-Control-Allow-Origin': '*',
        }
      }
    },
  },

  {
    route: '/refresh',
    fn({ options }) {
      const { tokens } = storeToRefs(useAuth())
      if (tokens.value) {
        options.headers = {
          ...options.headers,
          'Authorization': `Bearer ${tokens.value.refreshToken}`,
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

      const { setTokens, setUser } = useAuth()
      setTokens(null)
      setUser(null)
      useEventBus(RefreshTokenExpiredEvent).emit({ error })

      return Promise.resolve()
    },
  },
])
