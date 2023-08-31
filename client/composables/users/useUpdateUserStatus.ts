import type { MessageDTO } from '~/utils/messages'

export function useUpdateUserStatus() {
  const activate = useFeedbackCall((id: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${id}/activate`, { method: 'POST' })
  })
  const deactivate = useFeedbackCall((id: string) => {
    return $fetchAPI<MessageDTO<UserRepresentationDTO>>(`/users/${id}/deactivate`, { method: 'POST' })
  })

  return {
    activate(id: string) {
      return new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
        (activate(id) as Promise<void | MessageDTO<UserRepresentationDTO>>)
          .then(resolve)
          .catch(reject)
      })
    },
    deactivate(id: string) {
      return new Promise<void | MessageDTO<UserRepresentationDTO>>((resolve, reject) => {
        (deactivate(id) as Promise<void | MessageDTO<UserRepresentationDTO>>)
          .then(resolve)
          .catch(reject)
      })
    },
  }
}
