import type { DynamicDialogOptions } from 'primevue/dynamicdialogoptions'
import type { MessageDTO } from '~/utils/messages'
import { RenameDialog, RenameDialogFooter } from '#components'

export const useRenameFileDialog = defineStore('renameFileDialog', {
  state: () => ({ name: '' }),
})

export function useRenameFile() {
  const axios = useAxiosFactory()
  const { t } = useI18n()
  const call = useFeedbackCall((fileId: string, newName: string) => {
    return axios().put<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/name`, { name: newName })
  })
  const { selectedRow, selectedRowId, updateSelectedRow, unselectRow } = useFilesTable()
  const dialog = useDialog()

  const onClose = (options?: DynamicDialogOptions) => {
    if (!options?.data)
      return
    const newName = options?.data.newName as string
    if (!selectedRowId.value)
      return
    call(selectedRowId.value, newName)
      .then(response => updateSelectedRow(response?.data.payload))
      .finally(unselectRow)
  }

  return function renameFile() {
    dialog.open(RenameDialog, {
      props: {
        header: t('pages.files.rename.title'),
        modal: true,
        draggable: false,
        contentStyle: { 'min-width': '30em' },
      },
      data: {
        name: selectedRow.value?.name,
      },
      templates: {
        footer: markRaw(RenameDialogFooter),
      },
      onClose,
    })
  }
}
