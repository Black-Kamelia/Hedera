import type { MessageDTO } from '~/utils/messages'

export default function useUpdatePassword() {
  const { user } = storeToRefs(useAuth())

  const call = useFeedbackCall((oldPassword: string | null, newPassword: string, forced: boolean) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user.value!.id}/password`, {
      method: 'PATCH',
      body: { oldPassword, newPassword },
      params: { forced },
    })
  }, true)

  return (oldPassword: string | null, newPassword: string, forced = false) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(oldPassword, newPassword, forced) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
