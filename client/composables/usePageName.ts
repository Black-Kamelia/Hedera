const name = ref('Hedera')

export default function usePageName(pageName?: string) {
  onMounted(() => {
    const { t } = useI18n()

    if (pageName) {
      name.value = pageName
      useHead({
        title: `${pageName} • ${t('app_name')}`,
      })
    }
  })
  return name
}
