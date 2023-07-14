import type { Ref } from 'vue'

export interface UserSettingsStore {
  defaultFileVisibility: Ref<FileVisibility>
  autoRemoveFiles: Ref<boolean>
  filesSizeScale: Ref<FileSizeScale>
  preferredDateStyle: Ref<DateTimeStyle>
  preferredTimeStyle: Ref<DateTimeStyle>
  preferredLocale: Ref<Locale>
  updateSettings: (filters: Partial<UserSettings>) => void
}

export const useUserSettings = defineStore('userSettings', (): UserSettingsStore => {
  const locale = useLocale()

  const defaultFileVisibility = ref<FileVisibility>('UNLISTED')
  const autoRemoveFiles = ref<boolean>(false)
  const filesSizeScale = ref<FileSizeScale>('BINARY')
  const preferredDateStyle = ref<DateTimeStyle>('SHORT')
  const preferredTimeStyle = ref<DateTimeStyle>('MEDIUM')
  const preferredLocale = ref<Locale>('en')

  function updateSettings(settings: Partial<UserSettings>) {
    if (settings.defaultFileVisibility !== undefined) defaultFileVisibility.value = settings.defaultFileVisibility
    if (settings.autoRemoveFiles !== undefined) autoRemoveFiles.value = settings.autoRemoveFiles
    if (settings.filesSizeScale !== undefined) filesSizeScale.value = settings.filesSizeScale
    if (settings.preferredDateStyle !== undefined) preferredDateStyle.value = settings.preferredDateStyle
    if (settings.preferredTimeStyle !== undefined) preferredTimeStyle.value = settings.preferredTimeStyle
    if (settings.preferredLocale !== undefined) locale.value = preferredLocale.value = settings.preferredLocale
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
