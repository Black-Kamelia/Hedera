import type { MessageDTO } from '~/utils/messages'

export function useCreateToken() {
  const call = useFeedbackCall((tokenName: string) => {
    return $fetchAPI<MessageDTO<PersonalTokenDTO>>('/personalTokens', { method: 'POST', body: { name: tokenName } })
  })

  return function createToken(tokenName: string): Promise<void | MessageDTO<any>> {
    return new Promise((resolve, reject) => {
      call(tokenName).then(resolve).catch(reject)
    })
  }
}
