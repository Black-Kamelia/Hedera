import type { MessageDTO } from '~/utils/messages'
import { FileDeletedEvent } from '~/utils/events'

export default function useDeleteFile() {
  const fileDeletedEvent = useEventBus(FileDeletedEvent)
  const call = useFeedbackCall((fileId: string) => {
    return $fetchAPI<MessageDTO<FileRepresentationDTO>>(`/files/${fileId}`, { method: 'DELETE' })
  })
  const { selectedRowId, unselectRow, refresh } = useFilesTable()

  return function changeFileVisibility() {
    if (!selectedRowId.value) return

    call(selectedRowId.value)
      .then(() => fileDeletedEvent.emit())
      .then(refresh)
      .finally(unselectRow)
  }
}
