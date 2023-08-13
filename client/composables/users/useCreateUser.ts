import type { MessageDTO } from '~/utils/messages'

export function useCreateUser() {
  const call = useFeedbackCall((dto: UserCreationDTO) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>('/users', { method: 'POST', body: dto })
  })

  return function createToken(dto: UserCreationDTO): Promise<void | MessageDTO<UserRepresentationDTO>> {
    return new Promise((resolve, reject) => {
      (call(dto) as Promise<void | MessageDTO<UserRepresentationDTO>>)
        .then(resolve)
        .catch(reject)
    })
  }
}
