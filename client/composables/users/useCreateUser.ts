import type { MessageDTO } from '~/utils/messages'

export function useCreateUser() {
  const call = useFeedbackCall((dto: UserCreationDTO) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>('/users', { method: 'POST', body: dto })
  }, true)

  return (dto: UserCreationDTO) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(dto) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
