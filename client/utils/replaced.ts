export function replacedAt<T>(array: T[], index: number, item: T): T[] {
  return array.map((v, i) => (i === index ? item : v))
}

export function replaced<T>(array: T[], item: T, newItem: T): T[] {
  return array.map(v => (v === item ? newItem : v))
}

export function replacedBy<T>(array: T[], predicate: (item: T) => boolean, newItem: T): T[] {
  return array.map(v => (predicate(v) ? newItem : v))
}
