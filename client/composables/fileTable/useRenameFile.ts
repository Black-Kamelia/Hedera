import type { MessageDTO } from '~/utils/messages'

export default function useRenameFile() {
  const axios = useAxiosFactory()
  const call = useFeedbackCall((fileId: string, newName: string) => {
    return axios().put<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/name`, { name: newName })
  })
  const { selectedRowId, updateSelectedRow, unselectRow } = useFilesTable()

  return function renameFile(newName: string) {
    if (!selectedRowId.value)
      return

    call(selectedRowId.value, newName)
      .then(response => updateSelectedRow(response?.data.payload))
      .finally(unselectRow)
  }
}
