import type { MessageDTO } from '~/utils/messages'

export type FileVisibility = 'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE'

export default function useChangeFileVisibility() {
  const call = useFeedbackCall((fileId: string, visibility: FileVisibility) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/visibility`, { method: 'PUT', body: { visibility } })
  })
  const { selectedRowId, updateSelectedRow, unselectRow } = useFilesTable()

  return function changeFileVisibility(newVisibility: FileVisibility) {
    if (!selectedRowId.value)
      return

    call(selectedRowId.value, newVisibility)
      .then(response => updateSelectedRow(response?.payload as FileRepresentationDTO))
      .finally(unselectRow)
  }
}
