export function usernameRestrict(username: string): string {
  return username.toLowerCase().replace(/[^a-z0-9_\-.]/g, '')
}
