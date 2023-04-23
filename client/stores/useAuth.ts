import { AxiosError } from 'axios'

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
    try {
      const { data: tokens } = await axios().post<Tokens>('/login', values)
      setTokens(tokens)
      loggedInEvent.emit({ tokens })
    }
    catch (error) {
      if (error instanceof AxiosError) {
        loggedInEvent.emit({ error })
        return
      }
      throw error
    }
  }

  async function logout() {
    try {
      await axios().post('/logout')
      setTokens(null)
      loggedOutEvent.emit()
    }
    catch (error) {
      if (error instanceof AxiosError) {
        loggedOutEvent.emit({ error })
        return
      }
      throw error
    }
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
