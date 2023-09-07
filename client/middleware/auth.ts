const ANONYMOUS_ROUTES = ['/login', '/register', '/reset-password']

export default defineNuxtRouteMiddleware((to, from) => {
  const { isAuthenticated, user } = storeToRefs(useAuth())

  if (isAuthenticated.value && user.value!.forceChangePassword) {
    if (to.fullPath !== '/login?state=change-password') {
      return navigateTo('/login?state=change-password', { replace: true })
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
    return navigateTo('/login', { replace: true })
  }
})
