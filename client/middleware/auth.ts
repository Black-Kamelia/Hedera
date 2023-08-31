const ANONYMOUS_ROUTES = ['/login', '/register', '/reset-password']

export default defineNuxtRouteMiddleware((to, from) => {
  const { isAuthenticated } = storeToRefs(useAuth())

  if (ANONYMOUS_ROUTES.includes(to.path)) {
    if (isAuthenticated.value) {
      if (!ANONYMOUS_ROUTES.includes(from.path)) return abortNavigation()
      return navigateTo('/files', { replace: true })
    }
  } else if (!isAuthenticated.value) {
    if (ANONYMOUS_ROUTES.includes(from.path)) return abortNavigation()
    const redirect = to.path === '/' ? '' : `?redirect=${encodeURIComponent(to.path)}`
    return navigateTo(`/login${redirect}`, { replace: true })
  }
})
