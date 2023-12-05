const VALID_ROUTES = ['login', 'register', 'reset-password', 'update-password']

const SLIDE_LEFT = { name: 'card-slide-left' }
const SLIDE_RIGHT = { name: 'card-slide-right' }

const INDICES = {
  'register': -2,
  'reset-password': -1,
  'login': 0,
  'update-password': 1,
}

export default defineNuxtRouteMiddleware((to, from) => {
  if (!VALID_ROUTES.includes(to.name)) return
  if (!VALID_ROUTES.includes(from.name)) return

  const toIndex = INDICES[to.name as keyof typeof INDICES]
  const fromIndex = INDICES[from.name as keyof typeof INDICES]

  if (toIndex > fromIndex) {
    from.meta.pageTransition = SLIDE_LEFT
    to.meta.pageTransition = SLIDE_LEFT
  } else {
    from.meta.pageTransition = SLIDE_RIGHT
    to.meta.pageTransition = SLIDE_RIGHT
  }
})
