import type { MessageDTO } from '~/utils/messages'

export default function useUpdatePassword() {
  const { user } = useAuth()
  const { t, m } = useI18n()
  const toast = useToast()

  function updatePassword(oldPassword: string, newPassword: string) {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${user!.id}/password`, {
      method: 'PATCH',
      body: {
        oldPassword,
        newPassword,
      },
    }).then((response) => {
      toast.add({
        severity: 'success',
        summary: t('actions.users.update.password.success.message'),
        life: 5000,
      })
      return response
    }).catch((error) => {
      toast.add({
        severity: 'error',
        summary: m(error.response._data),
        life: 5000,
      })
      throw error
    })
  }

  return {
    updatePassword,
  }
}
