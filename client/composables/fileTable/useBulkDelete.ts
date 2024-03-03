import type { MessageDTO } from '~/utils/messages'
import useErrorToast from '~/composables/useErrorToast'
import useBulkActionResponseToast from '~/composables/useBulkActionResponseToast'

export function useBulkDelete() {
  const handleError = useErrorToast()
  const handleResponse = useBulkActionResponseToast()

  return function bulkDelete(fileIds: string[]) {
    return new Promise<void | MessageDTO<BulkActionSummaryDTO<string>>>((resolve, reject) => {
      $fetchAPI<MessageDTO<BulkActionSummaryDTO<string>>>('/files/bulk/delete', {
        method: 'POST',
        body: {
          ids: fileIds,
        },
      }).then(handleResponse)
        .catch(handleError)
        .then(resolve)
        .catch(reject)
    })
  }
}
