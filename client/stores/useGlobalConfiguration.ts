import type { Ref } from 'vue/dist/vue'
import { defineStore } from 'pinia'

export interface GlobalConfigurationStore {
  toggleRegistrations: Ref<boolean>
  defaultDiskQuotaPolicy: Ref<DiskQuotaPolicy>
  defaultDiskQuota: Ref<number | null>
  updateConfiguration: (configuration: Partial<GlobalConfiguration>) => void
  initConfiguration: () => void
}

export const useGlobalConfiguration = defineStore('globalConfiguration', (): GlobalConfigurationStore => {
  const toggleRegistrations = ref<boolean>(false)
  const defaultDiskQuotaPolicy = ref<DiskQuotaPolicy>('LIMITED')
  const defaultDiskQuota = ref<number | null>(524288000)

  function updateConfiguration(configuration: Partial<GlobalConfiguration>) {
    if (configuration.enableRegistrations !== undefined) toggleRegistrations.value = configuration.enableRegistrations
    if (configuration.defaultDiskQuotaPolicy !== undefined) defaultDiskQuotaPolicy.value = configuration.defaultDiskQuotaPolicy
    if (configuration.defaultDiskQuota !== undefined) defaultDiskQuota.value = configuration.defaultDiskQuota
  }

  function initConfiguration() {
    $fetchAPI<GlobalConfigurationRepresentationDTO>('/configuration').then(updateConfiguration)
  }

  return {
    toggleRegistrations,
    defaultDiskQuotaPolicy,
    defaultDiskQuota,
    updateConfiguration,
    initConfiguration,
  }
}, {
  persist: {
    storage: localStorage,
  },
})
