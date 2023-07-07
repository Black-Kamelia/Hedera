export default function useTheme() {
  const color = useColorMode()

  const head = computed(() => ({
    link: [
      {
        rel: 'stylesheet',
        href: color.value === 'dark' ? darkTheme : lightTheme,
      },
    ],
  }))
  useHead(head)
}
