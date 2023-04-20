const name = ref('Hedera')

export default function usePageName(pageName?: string) {
  onMounted(() => {
    if (pageName)
      name.value = pageName
  })
  return name
}
