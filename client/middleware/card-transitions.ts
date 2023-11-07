const VALID_ROUTES = ['login', 'register', 'reset-password']

const SLIDE_LEFT = { name: 'slide-left' }
const SLIDE_RIGHT = { name: 'slide-right' }

const INDICES = new Map<string, number>([
  ['register', -2],
  ['reset-password', -1],
  ['login', 0],
])

export default defineNuxtRouteMiddleware((to, from) => {
  if (!VALID_ROUTES.includes(to.name)) return
  if (!VALID_ROUTES.includes(from.name)) return

  const toIndex = INDICES.get(to.name)!
  const fromIndex = INDICES.get(from.name)!

  if (toIndex > fromIndex) {
    from.meta.pageTransition = SLIDE_LEFT
    to.meta.pageTransition = SLIDE_LEFT
  } else {
    from.meta.pageTransition = SLIDE_RIGHT
    to.meta.pageTransition = SLIDE_RIGHT
  }
})
