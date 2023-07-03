const name = ref<string>('')

export default function usePageName(pageName?: () => string) {
  const { t, locale } = useI18n()

  if (pageName)
    name.value = pageName()

  watch(locale, () => {
    name.value = pageName ? pageName() : ''
  })

  useHead({
    title: name,
    titleTemplate: (s) => {
      return s ? `${s} • ${t('app_name')}` : t('app_name')
    },
  })

  return name
}
