export const useCounterStore = defineStore('counter', () => {
  let count = $ref(0)
  const increment = () => count++
  return $$({ count, increment })
})
