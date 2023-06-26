export default function useTheme() {
  const { isDark } = $(useDark())

  const head = computed(() => ({
    link: [
      {
        rel: 'stylesheet',
        href: isDark ? darkTheme : lightTheme,
      },
    ],
  }))
  useHead(head)
}
