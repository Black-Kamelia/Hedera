import type { MessageDTO } from '~/utils/messages'

export default function useUpdatePassword() {
  const { user, isAuthenticated } = storeToRefs(useAuth())
  const websocketSession = useLocalStorage('websocketSession', null)

  if (!isAuthenticated.value) {
    throw new Error('User is not authenticated')
  }

  const call = useFeedbackCall((oldPassword: string | null, newPassword: string, forced: boolean) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user.value!.id}/password`, {
      method: 'PATCH',
      body: { oldPassword, newPassword },
      params: { forced, session: websocketSession.value },
    })
  })

  return (oldPassword: string | null, newPassword: string, forced = false) => new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
    (call(oldPassword, newPassword, forced) as Promise<void | MessageDTO<UserRepresentationDTO>>)
      .then(resolve)
      .catch(reject)
  })
}
