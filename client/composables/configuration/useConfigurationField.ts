import type { Ref } from 'vue'

export function useConfigurationField<T>(
  field: Ref<T>,
  mapField: (newField: T) => Partial<GlobalConfiguration>,
) {
  const { updateConfiguration } = useGlobalConfiguration()
  const toast = useToast()
  const { t, m } = useI18n()
  const isError = ref<boolean>(false)
  const backupValue = shallowRef<T>(field.value)

  watch(field, (_, oldValue) => {
    backupValue.value = oldValue
  })

  function patchConfiguration(newSetting: T) {
    isError.value = false
    return $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration', { method: 'PATCH', body: mapField(newSetting) })
      .then((response) => {
        updateConfiguration(response)
        return response
      })
      .catch((error) => {
        isError.value = true
        field.value = backupValue.value
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
    patchConfiguration,
    isError,
  }
}
