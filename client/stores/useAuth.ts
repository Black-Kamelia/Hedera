import { FetchError } from 'ofetch'
import { defineStore } from 'pinia'
import type { HederaUserConnectedPayload } from '~/utils/websocketEvents'

export interface User extends HederaUserConnectedPayload {}

export interface UseAuthComposer {
  user: Ref<Nullable<User>>
  tokens: Ref<Nullable<TokenData>>
  isAuthenticated: ComputedRef<boolean>

  setUser: (newUser: Nullable<Partial<User>>) => void
  setTokens: (newTokens: Nullable<Tokens>) => void
  login: (values: Record<string, any>) => Promise<void>
  logout: () => Promise<void>
}

export const useAuth = defineStore('auth', (): UseAuthComposer => {
  const loggedInEvent = useEventBus(LoggedInEvent)
  const loggedOutEvent = useEventBus(LoggedOutEvent)
  const { updateSettings } = useUserSettings()
  const { fetchConfiguration } = useGlobalConfiguration()

  const user = ref<Nullable<User>>(null)
  const tokens = ref<Nullable<Tokens>>(null)

  const isAuthenticated = computed(() => !!tokens.value)

  function setUser(newUser: Nullable<Partial<User>>) {
    if (!newUser) {
      user.value = null
      return
    }

    if (!user.value) {
      user.value = newUser as User
      return
    }

    Object.assign(user.value, newUser)
  }

  function setTokens(newTokens: Nullable<Tokens>) {
    tokens.value = newTokens
  }

  async function login(values: Record<string, any>) {
    try {
      const { tokens, user, userSettings } = await $fetchAPI<SessionOpeningDTO>('/login', { method: 'POST', body: values })
      setTokens(tokens)
      setUser(user)
      updateSettings(userSettings)
      if (!user.forceChangePassword && (user.role === 'ADMIN' || user.role === 'OWNER')) fetchConfiguration()
      loggedInEvent.emit({ tokens, user })
    } catch (error) {
      if (error instanceof FetchError) {
        loggedInEvent.emit({ error })
      }
      throw error
    }
  }

  async function logout() {
    try {
      await $fetchAPI('/logout', { method: 'POST' })
      setTokens(null)
      setUser(null)
      loggedOutEvent.emit()
    } catch (error) {
      if (error instanceof FetchError) {
        loggedOutEvent.emit({ error })
        return
      }
      throw error
    }
  }

  return {
    user,
    tokens,
    isAuthenticated,

    setUser,
    setTokens,
    login,
    logout,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
})
