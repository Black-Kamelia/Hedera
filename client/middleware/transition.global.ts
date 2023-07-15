const OUT_OF_APP_ROUTES = ['/login', '/register', '/reset-password']

const LAYOUT_OUT = { name: 'layout-out', appear: true }
const LAYOUT_IN = { name: 'layout-in', appear: true }

export default defineNuxtRouteMiddleware((to, from) => {
  if (OUT_OF_APP_ROUTES.includes(to.path)) {
    from.meta.layoutTransition = LAYOUT_OUT
    to.meta.layoutTransition = LAYOUT_OUT
  } else {
    from.meta.layoutTransition = LAYOUT_IN
    to.meta.layoutTransition = LAYOUT_IN
  }
})
