export default function useObjectURL(file: Blob | MediaSource) {
  const objectURL = computed(() => {
    return URL.createObjectURL(file)
  })
  onUnmounted(() => {
    if (objectURL.value) URL.revokeObjectURL(objectURL.value)
  })
  return objectURL
}
