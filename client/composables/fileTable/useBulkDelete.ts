import type { MessageDTO } from '~/utils/messages'
import { FileDeletedEvent } from '~/utils/events'

export function useBulkDelete() {
  const fileDeletedEvent = useEventBus(FileDeletedEvent)
  const call = useFeedbackCall((fileIds: string[]) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>('/files/bulk/delete', {
      method: 'POST',
      body: {
        ids: fileIds,
      },
    })
  })

  return function bulkDelete(fileIds: string[]) {
    return new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
      (call(fileIds) as Promise<void | MessageDTO<FileRepresentationDTO>>)
        .then(() => fileDeletedEvent.emit())
        .then(resolve)
        .catch(reject)
    })
  }
}
