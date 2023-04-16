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
  refresh: () => Promise<boolean>
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
        life: 5000,
      })
      navigateTo('/')
    }
    else {
      toast.add({
        severity: 'error',
        summary: 'Login',
        detail: 'Login failed',
        life: 5000,
      })
    }
  }

  async function refresh(): Promise<boolean> {
    const { data, error } = await executeRefresh()
    if (!error.value) {
      tokens.value = data.value
      return true
    }
    else {
      tokens.value = null
      return false
    }
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
      life: 5000,
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
