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

export const useAuth = defineStore('auth', () => {
  // const user = ref<Nullable<User>>(null)
  const tokens = ref<Nullable<Tokens>>(null)

  // function setUser(newUser: User) {
  //  user.value = newUser
  // }
  function setTokens(newTokens: Nullable<Tokens>) {
    tokens.value = newTokens
  }

  return { tokens, setTokens }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
})
