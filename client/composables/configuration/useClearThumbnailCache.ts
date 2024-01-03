export default function useClearThumbnailCache() {
  const { execute, status } = useFetchAPI('/configuration/maintenance/clear-thumbnail-cache', { method: 'POST', immediate: false })
  const pending = computed(() => status.value === 'pending')

  const { t } = useI18n()
  const confirm = useConfirm()

  function clearCache(): Promise<void> {
    return new Promise((resolve, reject) => {
      confirm.require({
        message: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.summary'),
        header: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.title'),
        acceptIcon: 'i-tabler-trash',
        acceptLabel: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.submit'),
        acceptClass: 'p-button-danger',
        rejectLabel: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.cancel'),
        accept() {
          return execute().then(resolve).catch(reject)
        },
      })
    })
  }

  return { clearCache, pending }
}
