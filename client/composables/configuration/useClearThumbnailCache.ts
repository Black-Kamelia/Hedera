import type { MessageDTO } from '~/utils/messages'

export default function useClearThumbnailCache() {
  const call = useFeedbackCall(() => {
    return $fetchAPI<MessageDTO<void>>('/configuration/maintenance/clear-thumbnail-cache', { method: 'POST' })
  })
  const { t } = useI18n()
  const confirm = useConfirm()

  function clearCache(): Promise<void | MessageDTO<void>> {
    return new Promise((resolve, reject) => {
      confirm.require({
        message: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.summary'),
        header: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.title'),
        acceptIcon: 'i-tabler-trash',
        acceptLabel: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.submit'),
        acceptClass: 'p-button-danger',
        rejectLabel: t('pages.configuration.maintenance.clear_thumbnail_cache_dialog.cancel'),
        accept() {
          return (call() as Promise<void | MessageDTO<void>>)
            .then(resolve)
            .catch(reject)
        },
      })
    })
  }

  return clearCache
}
