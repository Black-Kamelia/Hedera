export function useEmptyState(name: string) {
  const color = useColorMode()
  return computed(() => `/assets/img/empty_states/${color.value}/${name}.svg`)
}
