import type { MessageDTO } from '~/utils/messages'

export default function useDeleteToken() {
  const call = useFeedbackCall((tokenId: string) => {
    return $fetchAPI<MessageDTO<any>>(`/personalTokens/${tokenId}`, { method: 'DELETE' })
  })

  return function deleteToken(tokenId: string) {
    return call(tokenId)
  }
}
