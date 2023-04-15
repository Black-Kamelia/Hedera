import type { AxiosMiddlewares } from './types'

export const axiosMiddlewares: AxiosMiddlewares = {
  requestMiddlewares: [
    {
      route: /.*/,
      onRejected: (error) => {
        console.log('error 2', error)
      },
    },
  ],
  responseMiddlewares: [],
}
