import type { Ref } from 'vue'

// single parameter version
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

export function useConfigurationFields<T extends Record<string, Ref<any>>>(
  fields: T,
  mapFields: (newFields: T) => Partial<GlobalConfiguration>,
) {
  const { updateConfiguration } = useGlobalConfiguration()
  const toast = useToast()
  const { t, m } = useI18n()
  const isError = ref<boolean>(false)
  const backupValues = shallowRef<T>({} as T)

  // Initialize backupValues with the current values of the fields
  for (const key in fields) {
    backupValues.value[key] = fields[key].value
    watch(fields[key], (_, oldValue) => backupValues.value[key] = oldValue)
  }

  function patchConfiguration(newSettings: T) {
    isError.value = false
    const mapFieldsResult = mapFields(newSettings)
    return $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration', { method: 'PATCH', body: mapFieldsResult })
      .then((response) => {
        updateConfiguration(response)
        return response
      })
      .catch((error) => {
        isError.value = true

        // Restore the fields to their backup values
        for (const key in fields) {
          fields[key].value = backupValues.value[key]
        }

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
