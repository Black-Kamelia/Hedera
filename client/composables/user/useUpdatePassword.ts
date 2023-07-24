import type { MessageDTO } from '~/utils/messages'

export default function useUpdatePassword() {
  const { user } = useAuth()
  return useFeedbackCall((oldPassword: string, newPassword: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user!.id}/password`, {
      method: 'PATCH',
      body: {
        oldPassword,
        newPassword,
      },
    })
  })
}
