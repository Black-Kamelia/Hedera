import type { AxiosError } from 'axios'

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
  const axios = useAxiosFactory()

  const tokens = ref<Nullable<Tokens>>(null)

  const isAuthenticated = computed(() => !!tokens.value)

  function setTokens(newTokens: Nullable<Tokens>) {
    tokens.value = newTokens
  }

  async function login(values: Record<string, any>) {
    let tokens: Tokens
    try {
      const { data } = await axios().post<Tokens>('/login', values)
      tokens = data
    }
    catch (error) {
      loggedInEvent.emit({ error: error as AxiosError })
      return
    }

    setTokens(tokens)
    loggedInEvent.emit({ tokens })
  }

  async function logout() {
    try {
      await axios().post('/logout')
    }
    catch (error) {
      loggedOutEvent.emit({ error: error as AxiosError })
      return
    }

    setTokens(null)
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
