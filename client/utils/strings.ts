export function usernameRestrict(username: string): string {
  return username.toLowerCase().replace(/[^a-z0-9_\-.]/g, '')
}

export function slugRestrict(slug: string): string {
  return slug.toLowerCase().replace(' ', '-').replace(/[^a-z0-9\-]/g, '')
}
