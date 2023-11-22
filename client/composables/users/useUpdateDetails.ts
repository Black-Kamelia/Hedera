import type { MessageDTO } from '~/utils/messages'

export default function useUpdateDetails() {
  const { user, isAuthenticated } = storeToRefs(useAuth())

  if (!isAuthenticated.value) {
    throw new Error('User is not authenticated')
  }

  const call = useFeedbackCall((dto: Partial<UserUpdateDTO>) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user.value!.id}`, { method: 'PATCH', body: dto })
  }, true)

  return (dto: Partial<UserUpdateDTO>) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(dto) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
