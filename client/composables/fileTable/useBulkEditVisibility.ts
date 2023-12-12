import type { MessageDTO } from '~/utils/messages'

export function useBulkEditVisibility() {
  const call = useFeedbackCall((fileIds: string[], visibility: FileVisibility) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>('/files/bulk/visibility', {
      method: 'POST',
      body: {
        ids: fileIds,
        fileVisibility: visibility,
      },
    })
  })

  return function bulkEditVisibility(fileIds: string[], visibility: FileVisibility) {
    return new Promise<void | MessageDTO<void>>((resolve, reject) => {
      (call(fileIds, visibility) as Promise<void | MessageDTO<void>>)
        .then(resolve)
        .catch(reject)
    })
  }
}
