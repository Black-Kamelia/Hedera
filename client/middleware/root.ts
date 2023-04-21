export default defineNuxtRouteMiddleware(() => {
  const { isAuthenticated } = useAuth()
  if (!isAuthenticated.value)
    return navigateTo('/login')
  else
    return navigateTo('/files')
})
