function isISODate(value: string) {
  const data = new Date(value)
  return data.toISOString() === value
}

export const jsonDateSerializer = {
  serialize: JSON.stringify,
  deserialize(value: string) {
    return JSON.parse(value, (key, value) => {
      if (typeof value === 'string') {
        if (isISODate(value))
          return new Date(value)
      }
      return value
    })
  },
}
