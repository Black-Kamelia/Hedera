/**
 * Returns the redirect param for the query string
 *
 * @param path the path to redirect to
 * @param isFirstParam whether the redirect param is the first param in the query string, defaults to false
 */
export function getRedirectParam(path: string, isFirstParam = false) {
  const prefix = isFirstParam ? '?' : '&'
  return path === '/' ? '' : `${prefix}redirect=${encodeURIComponent(path)}`
}
