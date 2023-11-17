export default defineNuxtRouteMiddleware(async (to, from) => {
  if (to.path !== '/register') return

  function denyAccess() {
    if (from && from.path !== '/register') return abortNavigation()
    return navigateTo({
      path: '/login',
      query: { reason: 'registration_disabled' },
    }, { replace: true })
  }

  try {
    const config = await $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration/public')
    const registrationsEnabled = config.enableRegistrations

    if (!registrationsEnabled) return denyAccess()
  } catch (err) {
    return denyAccess()
  }
})
