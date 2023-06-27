export default function useDebug() {
  const isDebugEnabled = useLocalStorage<boolean>('debug', false, { writeDefaults: false })

  function close() {
    isDebugEnabled.value = false
    setTimeout(() => {
      localStorage.removeItem('debug')
    }, 50)
  }

  return {
    isDebugEnabled: readonly(isDebugEnabled),
    close,
  }
}
