import type { Tokens } from '~/stores/useAuth'

export function getTokensFromLocalStorage(): Tokens | null {
  const auth = localStorage.getItem('auth')
  if (!auth)
    return null

  const parsed = JSON.parse(auth)
  if (!parsed.tokens || !parsed.tokens.accessToken || !parsed.tokens.refreshToken)
    return null

  return parsed.tokens
}

export function clearTokensFromLocalStorage() {
  localStorage.removeItem('auth')
}
