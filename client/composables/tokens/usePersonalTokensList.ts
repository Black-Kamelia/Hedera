export interface PersonalTokensListContext {
  // updateSelectedRow: (newRow: FileRepresentationDTO) => void
  refresh: () => Promise<void>
}

export function usePersonalTokensList() {
  const context = inject(PersonalTokensListKey)

  if (!context) throw new Error('usePersonalTokensList() is called outside of PersonalTokensList hierarchy')

  return context
}
