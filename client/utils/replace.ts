export function replaceAt<T>(array: T[], index: number, item: T): T[] {
  return array.map((v, i) => (i === index ? item : v))
}
