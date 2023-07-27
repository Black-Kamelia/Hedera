import type { MessageDTO } from '~/utils/messages'

export default function useDeleteToken() {
  const call = useFeedbackCall((tokenId: string) => {
    return $fetchAPI<MessageDTO<any>>(`/personalTokens/${tokenId}`, { method: 'DELETE' })
  })
  const { refresh } = usePersonalTokensList()

  return function deleteToken(tokenId: string) {
    call(tokenId).then(refresh)
  }
}
