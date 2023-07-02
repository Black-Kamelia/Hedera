function isISODate(value: string) {
  const date = Date.parse(value)
  return !Number.isNaN(date) && new Date(date).toISOString() === value
}

export const jsonDateSerializer = {
  serialize: JSON.stringify,
  deserialize(value: string) {
    return JSON.parse(value, (_, value) => typeof value === 'string' && isISODate(value)
      ? new Date(value)
      : value)
  },
}
