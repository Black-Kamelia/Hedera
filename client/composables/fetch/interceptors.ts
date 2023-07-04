export const onRequestInterceptors = defineInterceptors<'onRequest'>([
  {
    route: '/api',
    fn({ request, options }) {
      const url = request.toString()
      console.log('onRequest', url, options) // eslint-disable-line no-console
    },
  },
])

export const onResponseInterceptors = defineInterceptors<'onResponse'>([])

export const onRequestErrorInterceptors = defineInterceptors<'onRequestError'>([])

export const onResponseErrorInterceptors = defineInterceptors<'onResponseError'>([])
