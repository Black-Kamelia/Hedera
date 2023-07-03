import type { MessageDTO } from '~/utils/messages'

export type FileVisibility = 'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE'

export default function useChangeFileVisibility() {
  const axios = useAxiosFactory()
  const call = useFeedbackCall((fileId: string, visibility: FileVisibility) => {
    return axios().put<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/visibility`, { visibility })
  })
  const { selectedRowId, updateSelectedRow, unselectRow } = useFilesTable()

  return function changeFileVisibility(newVisibility: FileVisibility) {
    if (!selectedRowId.value)
      return

    call(selectedRowId.value, newVisibility)
      .then(response => updateSelectedRow(response?.data.payload))
      .finally(unselectRow)
  }
}
