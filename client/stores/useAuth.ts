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
  login: (values: Record<string, any>) => Promise<void>
  logout: () => Promise<void>
}

export const useAuth = s<AuthReturn>(defineStore('auth', () => {
  const { execute } = useAPI('/login', { method: 'POST' })

  // const user = ref<Nullable<User>>(null)
  const tokens = ref<Nullable<Tokens>>(null)

  async function login(values: Record<string, any>) {
    const { data, error } = await execute({ data: values })
    if (!error.value) {
      tokens.value = data.value
      navigateTo('/')
    }
    else {
      console.error('Login failed')
    }
  }

  async function logout() {
    // TODO actual logout
    tokens.value = null
    navigateTo('/login')
  }

  return {
    tokens: readonly(tokens),

    login,
    logout,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
}))
