export interface Tokens {
  accessToken: string
  refreshToken: string
}

export interface UseAuthComposer {
  tokens: Ref<Nullable<Tokens>>
  isAuthenticated: ComputedRef<boolean>
  setTokens: (newTokens: Nullable<Tokens>) => void
  login: (values: Record<string, any>) => Promise<void>
  logout: () => Promise<void>
}

export const useAuth = s<UseAuthComposer>(defineStore('auth', (): UseAuthComposer => {
  const loggedInEvent = useEventBus(LoggedInEvent)
  const loggedOutEvent = useEventBus(LoggedOutEvent)

  const { execute: executeLogin } = useAPI('/login', { method: 'POST' }, { immediate: false })
  const { execute: executeLogout } = useAPI('/logout', { method: 'POST' }, { immediate: false })

  const tokens = ref<Nullable<Tokens>>(null)

  const isAuthenticated = computed(() => !!tokens.value)

  function setTokens(newTokens: Nullable<Tokens>) {
    tokens.value = newTokens
  }

  async function login(values: Record<string, any>) {
    const { data, error } = await executeLogin({ data: values })
    if (!error.value) {
      setTokens(data.value)
      loggedInEvent.emit({ success: true, tokens: data.value })
      navigateTo('/')
    }
    else {
      loggedInEvent.emit({ success: false })
    }
  }

  async function logout() {
    const { error } = await executeLogout()
    if (error.value)
      return

    setTokens(null)
    navigateTo('/login')
    loggedOutEvent.emit()
  }

  return {
    tokens,
    isAuthenticated,

    setTokens,
    login,
    logout,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
}))
