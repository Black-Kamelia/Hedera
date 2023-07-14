import type { MessageDTO } from '~/utils/messages'

export default function useDeleteFile() {
  const call = useFeedbackCall((fileId: string) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}`, { method: 'DELETE' })
  })
  const { selectedRowId, unselectRow, removeSelectedRow } = useFilesTable()

  return function changeFileVisibility() {
    if (!selectedRowId.value) return

    call(selectedRowId.value)
      .then(removeSelectedRow) // TODO : check if succeeded (callback?)
      .finally(unselectRow)
  }
}
