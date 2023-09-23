import type { MessageDTO } from '~/utils/messages'

export function useEditFileCustomLink() {
  const call = useFeedbackCall((id: string, dto: FileCustomLinkDTO) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/files/${id}/vanity-link`, { method: 'PUT', body: dto })
  })

  return (id: string, dto: FileCustomLinkDTO) => new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
    (call(id, dto) as Promise<void | MessageDTO<FileRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
