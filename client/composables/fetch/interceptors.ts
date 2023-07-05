export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: ['/refresh', '/login', '/users/signup', '/upload/token'],
    negateRoute: true,
    fn({ options }) {
      const { tokens } = storeToRefs(useAuth())
      if (tokens.value) {
        options.headers = {
          'Authorization': `Bearer ${tokens.value.accessToken}`,
          'Access-Control-Allow-Origin': '*',
          ...options.headers,
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
          'Authorization': `Bearer ${tokens.value.refreshToken}`,
          'Access-Control-Allow-Origin': '*',
          ...options.headers,
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
    fn({ response }) {
      if (response?.status !== 401)
        return

      const { setTokens, setUser } = useAuth()
      setTokens(null)
      setUser(null)
      useEventBus(RefreshTokenExpiredEvent).emit({ error: response._data })
    },
  },
])
