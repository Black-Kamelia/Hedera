import type { MessageDTO } from '~/utils/messages'

export default function useChangeFileVisibility() {
  const axios = useAxiosFactory()
  const call = useFeedbackCall((fileId: string, visibility: 'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE') => {
    return axios().put<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}/visibility`, { visibility })
  })
  const { selectedRowId, updateSelectedRow, unselectRow } = useFilesTable()

  return function changeFileVisibility(newName: string) {
    if (!selectedRowId.value)
      return

    call(selectedRowId.value, newName)
      .then(response => updateSelectedRow(response?.data.payload))
      .finally(unselectRow)
  }
}
