import type { Ref } from 'vue'

export interface UserSettingsStore {
  defaultFileVisibility: Ref<'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE'>
  autoRemoveFiles: Ref<boolean>
  filesSizeScale: Ref<'BINARY' | 'DECIMAL'>
  preferredDateStyle: Ref<'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'>
  preferredTimeStyle: Ref<'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'>
  preferredLocale: Ref<'en' | 'fr'>
  updateSettings: (filters: Partial<UserSettings>) => void
}

export const useUserSettings = defineStore('userSettings', (): UserSettingsStore => {
  const locale = useLocale()

  const defaultFileVisibility = ref<'PUBLIC' | 'UNLISTED' | 'PROTECTED' | 'PRIVATE'>('UNLISTED')
  const autoRemoveFiles = ref<boolean>(false)
  const filesSizeScale = ref<'BINARY' | 'DECIMAL'>('BINARY')
  const preferredDateStyle = ref<'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'>('SHORT')
  const preferredTimeStyle = ref<'SHORT' | 'MEDIUM' | 'LONG' | 'FULL'>('MEDIUM')
  const preferredLocale = ref<'en' | 'fr'>('en')

  function updateSettings(settings: Partial<UserSettings>) {
    if (settings.defaultFileVisibility !== undefined)
      defaultFileVisibility.value = settings.defaultFileVisibility

    if (settings.autoRemoveFiles !== undefined)
      autoRemoveFiles.value = settings.autoRemoveFiles

    if (settings.filesSizeScale !== undefined)
      filesSizeScale.value = settings.filesSizeScale

    if (settings.preferredDateStyle !== undefined)
      preferredDateStyle.value = settings.preferredDateStyle

    if (settings.preferredTimeStyle !== undefined)
      preferredTimeStyle.value = settings.preferredTimeStyle

    if (settings.preferredLocale !== undefined)
      locale.value = preferredLocale.value = settings.preferredLocale
  }

  return {
    defaultFileVisibility,
    autoRemoveFiles,
    filesSizeScale,
    preferredDateStyle,
    preferredTimeStyle,
    preferredLocale,
    updateSettings,
  }
}, {
  persist: {
    storage: persistedState.localStorage,
  },
})
