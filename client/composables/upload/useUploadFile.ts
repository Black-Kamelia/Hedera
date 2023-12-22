import type { MessageDTO } from '#imports'

export function useUploadFile() {
  return function (file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>('/files/upload', {
      method: 'POST',
      body: formData,
      headers: { 'Content-Disposition': formData as any },
    })
  }
}
