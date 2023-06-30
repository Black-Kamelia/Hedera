import type { ComputedRef, Ref } from 'vue'

export interface FilesTableContext {
  selectedRow: Readonly<Ref<Nullable<FileRepresentationDTO>>>
  selectedRowId: ComputedRef<string | undefined>
  updateSelectedRow: (newRow: FileRepresentationDTO) => void
  removeSelectedRow: () => void
  unselectRow: () => void
}

export default function useFilesTable() {
  const context = inject<FilesTableContext>(fileTableKey)!

  if (!context)
    throw new Error('useFilesTable() is called outside of FilesTable hierarchy')

  return context
}
