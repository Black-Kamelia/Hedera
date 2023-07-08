const skipRefreshRoutes = ['/refresh', '/login', '/users/signup', '/upload/token']

export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: skipRefreshRoutes,
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
])

export const onResponseInterceptors = defineInterceptors<'onResponse'>([])

export const onRequestErrorInterceptors = defineInterceptors<'onRequestError'>([])

export const onResponseErrorInterceptors = defineInterceptors<'onResponseError'>([])
