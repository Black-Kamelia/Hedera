export default defineNuxtRouteMiddleware((to) => {
  const { isAuthenticated } = useAuth()

  if ((to.path === '/login' || to.path === '/register' || to.path === '/resetPassword')) {
    if (isAuthenticated.value)
      return navigateTo('/files', { replace: true })
  }
  else if (!isAuthenticated.value) {
    return navigateTo('/login', { replace: true })
  }

  // continue
})
