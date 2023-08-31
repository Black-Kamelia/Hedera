/**
 * Awaitable sleep delay in milliseconds.
 *
 * Usage: `await sleep(1000)`
 *
 * @param ms milliseconds to delay
 */
export function sleep(ms: number): Promise<void> {
  return new Promise(resolve => setTimeout(resolve, ms))
}
