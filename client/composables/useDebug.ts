export default function useDebug() {
  const isDebugEnabled = useLocalStorage<boolean>('debug', false, { writeDefaults: false })

  function close() {
    // isDebugEnabled.value = false
    localStorage.removeItem('debug')
  }

  return {
    isDebugEnabled,
    close,
  }
}
