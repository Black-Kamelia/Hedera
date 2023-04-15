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
  isAuthed: ComputedRef<boolean>
  login: (values: Record<string, any>) => Promise<void>
  refresh: () => Promise<void>
  logout: () => Promise<void>
}

export const useAuth = s<AuthReturn>(defineStore('auth', () => {
  const toast = useToast()

  const { execute: executeLogin } = useAPI('/login', { method: 'POST' })
  const { execute: executeRefresh } = useAPI('/refresh', { method: 'POST' })
  const { execute: executeLogout } = useAPI('/logout', { method: 'POST' })

  // const user = ref<Nullable<User>>(null)
  const tokens = ref<Nullable<Tokens>>(null)

  const isAuthed = computed(() => !!tokens.value)

  async function login(values: Record<string, any>) {
    const { data, error } = await executeLogin({ data: values })
    if (!error.value) {
      tokens.value = data.value
      toast.add({
        severity: 'success',
        summary: 'Login',
        detail: 'Login successful',
      })
      navigateTo('/')
    }
    else {
      toast.add({
        severity: 'error',
        summary: 'Login',
        detail: 'Login failed',
      })
    }
  }

  async function refresh() {
    const { data, error } = await executeRefresh()
    if (!error.value)
      tokens.value = data.value
    else
      throw new Error('Refresh failed') // Session expired
  }

  async function logout() {
    const { error } = await executeLogout()
    if (error.value)
      return

    tokens.value = null
    navigateTo('/login')
    toast.add({
      severity: 'success',
      summary: 'Logout',
      detail: 'Logout successful',
    })
  }

  return {
    tokens,
    isAuthed,

    login,
    refresh,
    logout,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
}))
