const ANONYMOUS_ROUTES = ['/login', '/register', '/reset-password']

export default defineNuxtRouteMiddleware((to, from) => {
  if (ANONYMOUS_ROUTES.includes(to.path)) {
    from.meta.layoutTransition = { name: 'layout-out', appear: true }
    to.meta.layoutTransition = { name: 'layout-out', appear: true }
  } else {
    from.meta.layoutTransition = { name: 'layout-in', appear: true }
    to.meta.layoutTransition = { name: 'layout-in', appear: true }
  }
})
