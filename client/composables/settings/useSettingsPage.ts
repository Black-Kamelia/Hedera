import type { AxiosResponse } from 'axios'

export interface UserSettingsContext {
  patchSettings: (newSettings: Partial<UserSettings>) => Promise<void | AxiosResponse<UserSettings>>
}

export function useSettingsPage() {
  const context = inject(UserSettingsKey)

  if (!context)
    throw new Error('useSettingsPage() is called outside of user settings page hierarchy')

  return context
}
