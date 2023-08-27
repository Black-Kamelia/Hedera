const ALLOWED_ROLES = ['ADMIN', 'OWNER']

export default defineNuxtRouteMiddleware(() => {
  const { user } = storeToRefs(useAuth())

  if (!user.value) abortNavigation()

  if (!ALLOWED_ROLES.includes(user.value!.role)) {
    return navigateTo('/files', { replace: true })
  }
})
