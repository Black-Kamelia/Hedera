import type { MessageDTO } from '~/utils/messages'

export function useEditFileCustomLink() {
  const editLink = useFeedbackCall((id: string, customLink: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/files/${id}/custom-link`, { method: 'PUT', body: { customLink } })
  })
  const deleteLink = useFeedbackCall((id: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/files/${id}/custom-link`, { method: 'DELETE' })
  })

  function editCustomLink(id: string, customLink: string) {
    return new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
      (editLink(id, customLink) as Promise<void | MessageDTO<FileRepresentationDTO>>)
        .then(resolve)
        .catch(reject)
    })
  }

  function deleteCustomLink(id: string) {
    return new Promise<void | MessageDTO<FileRepresentationDTO>>((resolve, reject) => {
      (deleteLink(id) as Promise<void | MessageDTO<FileRepresentationDTO>>)
        .then(resolve)
        .catch(reject)
    })
  }

  return { editCustomLink, deleteCustomLink }
}
