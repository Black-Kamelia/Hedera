export const IGNORE_REFRESH_ROUTES = ['/api/refresh', '/api/login', '/api/users/signup', '/api/upload/token']

export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: IGNORE_REFRESH_ROUTES,
    negateRoute: true,
    fn({ options }) {
      const { tokens } = storeToRefs(useAuth())
      if (tokens.value) {
        options.headers = {
          Authorization: `Bearer ${tokens.value.accessToken}`,
          ...options.headers,
        }
      }
    },
  },
])

export const onResponseInterceptors = defineInterceptors<'onResponse'>([])

export const onRequestErrorInterceptors = defineInterceptors<'onRequestError'>([
  {
    route: '/api/logout',
    fn() {
      // act as if logout is successful anyway
      const logoutEvent = useEventBus(LoggedOutEvent)
      const auth = useAuth()
      auth.setUser(null)
      auth.setTokens(null)
      logoutEvent.emit()
    },
  },
])

export const onResponseErrorInterceptors = defineInterceptors<'onResponseError'>([
  {
    route: '/api/logout',
    fn() {
      // act as if logout is successful anyway
      const logoutEvent = useEventBus(LoggedOutEvent)
      const auth = useAuth()
      auth.setUser(null)
      auth.setTokens(null)
      logoutEvent.emit()
    },
  },
])
