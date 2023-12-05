const ANONYMOUS_ROUTES = ['/login', '/register', '/reset-password']

export default defineNuxtRouteMiddleware((to, from) => {
  const { isAuthenticated, user } = storeToRefs(useAuth())

  if (isAuthenticated.value && user.value!.forceChangePassword) {
    if (to.path !== '/update-password') {
      const redirect = to.query.redirect ?? to.path
      return navigateTo({
        path: '/update-password',
        query: { redirect },
      }, { replace: true })
    }
    return
  }

  if (ANONYMOUS_ROUTES.includes(to.path)) {
    if (isAuthenticated.value) {
      if (!ANONYMOUS_ROUTES.includes(from.path)) return abortNavigation()
      return navigateTo('/files', { replace: true })
    }
  } else if (!isAuthenticated.value) {
    if (ANONYMOUS_ROUTES.includes(from.path)) return abortNavigation()
    const redirect = to.query.redirect ?? to.path
    return navigateTo({
      path: '/login',
      query: { redirect },
    }, { replace: true })
  }
})
