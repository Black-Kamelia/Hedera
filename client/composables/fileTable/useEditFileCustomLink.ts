import type { MessageDTO } from '~/utils/messages'

export function useEditFileCustomLink() {
  const call = useFeedbackCall((id: string, customLink: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/files/${id}/custom-link`, { method: 'PUT', body: { customLink } })
  })

  return (id: string, customLink: string) => new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
    (call(id, customLink) as Promise<void | MessageDTO<FileRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
