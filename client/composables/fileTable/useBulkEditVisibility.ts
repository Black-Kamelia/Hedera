import type { MessageDTO } from '~/utils/messages'
import useErrorToast from '~/composables/useErrorToast'

export function useBulkEditVisibility() {
  const { m } = useI18n()
  const toast = useToast()
  const handleError = useErrorToast()

  return function bulkEditVisibility(fileIds: string[], visibility: FileVisibility) {
    return new Promise<void | MessageDTO<BulkActionSummaryDTO>>((resolve, reject) => {
      $fetchAPI<MessageDTO<BulkActionSummaryDTO>>('/files/bulk/visibility', {
        method: 'POST',
        body: {
          ids: fileIds,
          fileVisibility: visibility,
        },
      }).then((response) => {
        if (response.payload!.successes > 0 && response.payload!.fails === 0) {
          toast.add({
            severity: 'success',
            summary: m(response.title),
            detail: { text: response.message ? m(response.message) : null },
            life: 5000,
          })
        } else if (response.payload!.successes > 0 && response.payload!.fails > 0) {
          toast.add({
            severity: 'warn',
            summary: m(response.title),
            detail: { text: response.message ? m(response.message) : null },
            life: 5000,
          })
        } else if (response.payload!.successes === 0 && response.payload!.fails > 0) {
          toast.add({
            severity: 'error',
            summary: m(response.title),
            detail: { text: response.message ? m(response.message) : null },
            life: 5000,
          })
        }
      }).catch(handleError)
        .then(resolve)
        .catch(reject)
    })
  }
}
