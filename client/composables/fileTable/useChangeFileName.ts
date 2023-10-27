import type { MessageDTO } from '~/utils/messages'

export function useEditFileName() {
  const call = useFeedbackCall((fileId: string, newName: string) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/name`, { method: 'PUT', body: { name: newName } })
  })

  return function editFileName(fileId: string, newFilename: string) {
    return new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
      (call(fileId, newFilename) as Promise<void | MessageDTO<FileRepresentationDTO>>)
        .then(resolve)
        .catch(reject)
    })
  }
}
