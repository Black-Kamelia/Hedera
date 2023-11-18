import type { Ref } from 'vue'
import useErrorToast from '~/composables/useErrorToast'

export function useSetting<T>(
  setting: Ref<T>,
  mapSetting: (newSetting: T) => Partial<UserSettings>,
) {
  const { updateSettings } = useUserSettings()
  const handleError = useErrorToast()
  const isError = ref<boolean>(false)
  const backupValue = shallowRef<T>(setting.value)

  watch(setting, (_, oldValue) => {
    backupValue.value = oldValue
  })

  function patchSetting(newSetting: T) {
    isError.value = false
    return $fetchAPI<UserSettings>('/users/settings', { method: 'PATCH', body: mapSetting(newSetting) })
      .then((response) => {
        updateSettings(response)
        return response
      })
      .catch((error) => {
        isError.value = true
        setting.value = backupValue.value
        handleError(error)
      })
  }

  return {
    patchSetting,
    isError,
  }
}
