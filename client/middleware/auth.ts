const ANONYMOUS_ROUTES = ['/login', '/register', '/reset-password']

export default defineNuxtRouteMiddleware((to, from) => {
  const { isAuthenticated } = useAuth()

  if (ANONYMOUS_ROUTES.includes(to.path)) {
    if (isAuthenticated.value) {
      if (!ANONYMOUS_ROUTES.includes(from.path))
        return abortNavigation()
      return navigateTo('/files', { replace: true })
    }
  }
  else if (!isAuthenticated.value) {
    if (ANONYMOUS_ROUTES.includes(from.path))
      return abortNavigation()
    return navigateTo('/login', { replace: true })
  }
})
