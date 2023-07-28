import type { MessageDTO } from '~/utils/messages'
import { CreateTokenDialog, CreateTokenDialogFooter } from '#components'

export function useCreateToken() {
  const { t } = useI18n()
  const call = useFeedbackCall((tokenName: string) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>('/personalTokens', { method: 'POST', body: { name: tokenName } })
  })
  const dialog = useDialog()

  return function createToken(): Promise<void | MessageDTO<any>> {
    return new Promise((resolve, reject) => {
      return dialog.open(CreateTokenDialog, {
        props: {
          header: t('pages.profile.tokens.create_dialog.title'),
          modal: true,
          draggable: false,
        },
        templates: {
          footer: markRaw(CreateTokenDialogFooter),
        },
        onClose(options) {
          if (options?.data) {
            return call(options.data.name).then(resolve).catch(reject)
          } else {
            return resolve()
          }
        },
      })
    })
  }
}
