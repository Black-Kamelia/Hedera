import type { MessageDTO } from '~/utils/messages'

export function useDeleteUser() {
  const call = useFeedbackCall((id: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${id}`, { method: 'DELETE' })
  })

  return (id: string) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(id) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
