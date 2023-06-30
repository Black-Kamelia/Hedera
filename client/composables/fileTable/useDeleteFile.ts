import type { MessageDTO } from '~/utils/messages'

export default function useDeleteFile() {
  const axios = useAxiosFactory()
  const call = useFeedbackCall((fileId: string) => {
    return axios().delete<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}`)
  })
  const { selectedRowId, unselectRow, removeSelectedRow } = useFilesTable()

  return function changeFileVisibility() {
    if (!selectedRowId.value)
      return

    call(selectedRowId.value)
      .then(removeSelectedRow) // TODO : check if succeeded (callback?)
      .finally(unselectRow)
  }
}
