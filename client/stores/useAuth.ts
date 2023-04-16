// export interface User {
//   id: string
//   email: string
//   username: string
//   role: string
// }

export interface Tokens {
  accessToken: string
  refreshToken: string
}

export interface AuthReturn {
  tokens: Ref<Nullable<Tokens>>
  isAuthenticated: ComputedRef<boolean>
  setTokens: (newTokens: Nullable<Tokens>) => void
  login: (values: Record<string, any>) => Promise<void>
  logout: () => Promise<void>
}

export const useAuth = s<AuthReturn>(defineStore('auth', (): AuthReturn => {
  const toast = useToast()

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
      toast.add({
        severity: 'success',
        summary: 'Login',
        detail: 'Login successful',
        life: 3000,
        closable: true,
      })
      navigateTo('/')
    }
    else {
      toast.add({
        severity: 'error',
        summary: 'Login',
        detail: 'Login failed',
        life: 3000,
        closable: true,
      })
    }
  }

  async function logout() {
    const { error } = await executeLogout()
    if (error.value)
      return

    setTokens(null)
    navigateTo('/login')
    toast.add({
      severity: 'success',
      summary: 'Logout',
      detail: 'Logout successful',
      life: 3000,
      closable: true,
    })
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
