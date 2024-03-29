import type { ComputedRef, Ref } from 'vue'

export interface FilesTableContext {
  selectedRow: Readonly<Ref<Nullable<FileRepresentationDTO>>>
  selectedRowId: ComputedRef<string | undefined>
  updateSelectedRow: (newRow: FileRepresentationDTO) => void
  unselectRow: () => void
  refresh: () => Promise<any>
}

export function useFilesTable() {
  const context = inject(FileTableKey)

  if (!context) throw new Error('useFilesTable() is called outside of FilesTable hierarchy')

  return context
}
