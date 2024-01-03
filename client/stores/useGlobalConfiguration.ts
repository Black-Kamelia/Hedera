import type { Ref } from 'vue'
import { defineStore } from 'pinia'

export interface GlobalConfigurationStore {
  toggleRegistrations: Ref<boolean>
  defaultDiskQuotaPolicy: Ref<DiskQuotaPolicy>
  defaultDiskQuota: Ref<number | null>
  maximumThumbnailCount: Ref<number>

  updateConfiguration: (configuration: Partial<GlobalConfiguration>) => void
  fetchConfiguration: () => void
}

export const useGlobalConfiguration = defineStore('globalConfiguration', (): GlobalConfigurationStore => {
  const toggleRegistrations = ref<boolean>(false)
  const defaultDiskQuotaPolicy = ref<DiskQuotaPolicy>('LIMITED')
  const defaultDiskQuota = ref<number | null>(524288000) // 500 MiB
  const maximumThumbnailCount = ref<number>(150)

  function updateConfiguration(configuration: Partial<GlobalConfiguration>) {
    if (configuration.enableRegistrations !== undefined) toggleRegistrations.value = configuration.enableRegistrations
    if (configuration.defaultDiskQuotaPolicy !== undefined) defaultDiskQuotaPolicy.value = configuration.defaultDiskQuotaPolicy
    if (configuration.defaultDiskQuota !== undefined) defaultDiskQuota.value = configuration.defaultDiskQuota
    if (configuration.maximumThumbnailCount !== undefined) maximumThumbnailCount.value = configuration.maximumThumbnailCount
  }

  function fetchConfiguration() {
    $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration').then(updateConfiguration)
  }

  return {
    toggleRegistrations,
    defaultDiskQuotaPolicy,
    defaultDiskQuota,
    maximumThumbnailCount,

    updateConfiguration,
    fetchConfiguration,
  }
}, {
  persist: {
    storage: localStorage,
  },
})
