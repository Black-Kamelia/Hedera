import type { ComputedRef, Ref } from 'vue'

export interface UsersTableContext {
  selectedRow: Readonly<Ref<Nullable<UserRepresentationDTO>>>
  selectedRowId: ComputedRef<string | undefined>
  unselectRow: () => void
  refresh: () => Promise<void>
}

export function useUsersTable() {
  const context = inject(UsersTableKey)

  if (!context) throw new Error('useUsersTable() is called outside of UsersTable hierarchy')

  return context
}
