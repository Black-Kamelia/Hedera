export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: ['/refresh', '/login', '/users/signup', '/upload/token'],
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
