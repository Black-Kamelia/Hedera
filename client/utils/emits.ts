export function $defineEmits<T extends Record<string, any>>(): SE<T> {
  return defineEmits<SE<T>>()
}
