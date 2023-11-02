export default function useTheme() {
  const color = useColorMode()

  const head = computed(() => {
    const theme = color.value === 'dark' ? darkTheme : lightTheme
    return {
      link: [
        {
          rel: 'stylesheet',
          href: theme.main,
        },
        {
          rel: 'stylesheet',
          href: theme.custom,
        },
      ],
    }
  })
  useHead(head)
}
