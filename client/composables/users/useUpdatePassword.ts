import type { MessageDTO } from '~/utils/messages'

export default function useUpdatePassword() {
  const { user, isAuthenticated } = storeToRefs(useAuth())

  if (!isAuthenticated.value) {
    throw new Error('User is not authenticated')
  }

  const call = useFeedbackCall((oldPassword: string | null, newPassword: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user.value!.id}/password`, {
      method: 'PATCH',
      body: { oldPassword, newPassword },
    })
  })

  return (oldPassword: string | null, newPassword: string) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(oldPassword, newPassword) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
