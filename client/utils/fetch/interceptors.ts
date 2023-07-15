export const IGNORE_REFRESH_ROUTES = ['/refresh', '/login', '/users/signup', '/upload/token']

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

export const onRequestErrorInterceptors = defineInterceptors<'onRequestError'>([])

export const onResponseErrorInterceptors = defineInterceptors<'onResponseError'>([])
