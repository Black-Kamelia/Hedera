import type { Tokens } from '~/stores/useAuth'

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

  {
    route: skipRefreshRoutes,
    negateRoute: true,
    async fn({ request, response, options }) {
      if (response?.status !== 401)
        return

      useEventBus(AccessTokenExpiredEvent).emit({ error: response._data })

      const tokens = await $fetch<Tokens>('/refresh', { method: 'post' })
      const { setTokens } = useAuth()
      setTokens(tokens)

      // retry request
      await $fetch(request.toString(), response)
    },
  },
])
