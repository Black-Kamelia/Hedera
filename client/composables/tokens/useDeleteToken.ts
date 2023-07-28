import type { MessageDTO } from '~/utils/messages'

export default function useDeleteToken() {
  const call = useFeedbackCall((tokenId: string) => {
    return $fetchAPI<MessageDTO<PersonalTokenDTO>>(`/personalTokens/${tokenId}`, { method: 'DELETE' })
  })
  const { t } = useI18n()
  const confirm = useConfirm()

  function deleteToken(tokenId: string) {
    return new Promise((resolve, reject) => {
      confirm.require({
        message: t('pages.profile.tokens.delete_dialog.summary'),
        header: t('pages.profile.tokens.delete_dialog.title'),
        acceptIcon: 'i-tabler-trash',
        acceptLabel: t('pages.profile.tokens.delete_dialog.submit'),
        acceptClass: 'p-button-danger',
        rejectLabel: t('pages.profile.tokens.delete_dialog.cancel'),
        accept() {
          return call(tokenId).then(resolve).catch(reject)
        },
      })
    })
  }

  return deleteToken
}
