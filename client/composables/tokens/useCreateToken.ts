import type { MessageDTO } from '~/utils/messages'

export function useCreateToken() {
  const call = useFeedbackCall((tokenName: string) => {
    return $fetchAPI<MessageDTO<PersonalTokenDTO>>('/personalTokens', { method: 'POST', body: { name: tokenName } })
  })

  return function createToken(tokenName: string): Promise<void | MessageDTO<PersonalTokenDTO>> {
    return new Promise((resolve, reject) => {
      (call(tokenName) as Promise<void | MessageDTO<PersonalTokenDTO>>)
        .then(resolve)
        .catch(reject)
    })
  }
}
