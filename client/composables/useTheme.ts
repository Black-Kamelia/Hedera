export default function useTheme() {
  const { isDark } = useDark()

  const head = computed(() => ({
    link: [
      {
        rel: 'stylesheet',
        href: isDark.value ? darkTheme : lightTheme,
      },
    ],
  }))
  useHead(head)
}
