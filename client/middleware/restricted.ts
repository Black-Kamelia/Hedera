export default defineNuxtRouteMiddleware((to, from) => {
  const { user } = storeToRefs(useAuth())

  if (to.path.startsWith('/configuration') && user.value?.role === 'REGULAR') {
    if (!from.path.startsWith('/configuration')) return abortNavigation()
    return navigateTo('/files', { replace: true })
  }
})
