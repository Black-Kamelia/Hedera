import type { MessageDTO } from '~/utils/messages'
import useErrorToast from '~/composables/useErrorToast'
import useBulkActionResponseToast from '~/composables/useBulkActionResponseToast'

export function useBulkEditVisibility() {
  const handleError = useErrorToast()
  const handleResponse = useBulkActionResponseToast()

  return function bulkEditVisibility(fileIds: string[], visibility: FileVisibility) {
    return new Promise<void | MessageDTO<BulkActionSummaryDTO<string>>>((resolve, reject) => {
      $fetchAPI<MessageDTO<BulkActionSummaryDTO<string>>>('/files/bulk/visibility', {
        method: 'POST',
        body: {
          ids: fileIds,
          fileVisibility: visibility,
        },
      }).then(handleResponse)
        .catch(handleError)
        .then(resolve)
        .catch(reject)
    })
  }
}
