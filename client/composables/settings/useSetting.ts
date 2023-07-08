import type { Ref } from 'vue'

export function useSetting<T>(
  setting: Ref<T>,
  mapSetting: (newSetting: T) => Partial<UserSettings>,
) {
  const { updateSettings } = useUserSettings()
  const toast = useToast()
  const { t, m } = useI18n()
  const isError = ref<boolean>(false)
  const backupValue = shallowRef<T>(setting.value)

  watch(setting, (_, oldValue) => {
    backupValue.value = oldValue
  })

  function patchSetting(newSetting: T) {
    isError.value = false
    return $fetchAPI<UserSettings>('/users/settings', { method: 'patch', body: mapSetting(newSetting) })
      .then((response) => {
        updateSettings(response)
        return response
      })
      .catch((error) => {
        isError.value = true
        setting.value = backupValue.value
        if (!error.response) {
          toast.add({
            severity: 'error',
            summary: t('errors.unknown'),
            detail: { text: t('errors.network') },
            life: 5000,
          })
          return
        }
        toast.add({
          severity: 'error',
          summary: t('error'), // TODO: get error title from backend
          detail: { text: m(error) },
          life: 5000,
        })
      })
  }

  return {
    patchSetting,
    isError,
  }
}
