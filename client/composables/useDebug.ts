export default function useDebug() {
  const isDebugEnabled = useLocalStorage<boolean | undefined>('debug', undefined, { writeDefaults: false })

  function close() {
    isDebugEnabled.value = false
    isDebugEnabled.value = undefined
  }

  return {
    isDebugEnabled: readonly(isDebugEnabled),
    close,
  }
}
