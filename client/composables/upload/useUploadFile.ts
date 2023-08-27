import type { MessageDTO } from 'utils/messages'

export function useUploadFile() {
  const call = useFeedbackCall((file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>('/files/upload', {
      method: 'POST',
      body: formData,
      headers: { 'Content-Disposition': formData as any },
    })
  })

  return (file: File) => new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
    (call(file) as Promise<void | MessageDTO<FileRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
