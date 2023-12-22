import type { MessageDTO } from '~/utils/messages'

export function useUpdateUser() {
  const call = useFeedbackCall((id: string, dto: Partial<UserCreationDTO>) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${id}`, { method: 'PATCH', body: dto })
  }, true)

  return (id: string, dto: Partial<UserCreationDTO>) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(id, dto) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
