import type { MessageDTO } from '~/utils/messages'
import useErrorToast from '~/composables/useErrorToast'

export function useBulkDelete() {
  const { m } = useI18n()
  const toast = useToast()
  const handleError = useErrorToast()

  return function bulkDelete(fileIds: string[]) {
    return new Promise<void | MessageDTO<BulkActionSummaryDTO>>((resolve, reject) => {
      $fetchAPI<MessageDTO<BulkActionSummaryDTO>>('/files/bulk/delete', {
        method: 'POST',
        body: {
          ids: fileIds,
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
