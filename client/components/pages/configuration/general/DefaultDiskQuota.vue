<script setup lang="ts">
const {
  quotaPolicyValue,
  quotaValue,
} = defineProps<{
  quotaPolicyValue: DiskQuotaPolicy
  quotaValue: number | null
}>()
const quotaPolicy = ref(quotaPolicyValue)
const quota = ref<number | null>(quotaValue)

const { t } = useI18n()
const { patchConfiguration, isError } = useConfigurationFields({
  defaultDiskQuotaPolicy: quotaPolicy,
  defaultDiskQuota: quota,
}, ({ defaultDiskQuotaPolicy, defaultDiskQuota }) => ({
  defaultDiskQuotaPolicy: defaultDiskQuotaPolicy.value,
  defaultDiskQuota: defaultDiskQuota.value,
}))

const options = computed(() => [
  { icon: 'i-tabler-infinity', name: t('pages.configuration.general.default_disk_quota.unlimited'), value: 'UNLIMITED' },
  { icon: 'i-tabler-database', name: t('pages.configuration.general.default_disk_quota.limited'), value: 'LIMITED' },
])
const inputPlaceholder = computed(() => {
  if (quotaPolicy.value === 'UNLIMITED') return t('unlimited_quota')
  return t('pages.configuration.general.default_disk_quota.placeholder')
})

function getOption(value: string) {
  return options.value.find(option => option.value === value)
}

function patch(policyValue?: Nullable<DiskQuotaPolicy>, quotaValue?: Nullable<number>) {
  if (policyValue) quotaPolicy.value = policyValue
  if (quotaValue !== undefined) quota.value = quotaValue

  if (quotaPolicy.value === 'UNLIMITED') quota.value = null
  if (quotaPolicy.value === 'LIMITED' && quota.value === null) return

  patchConfiguration({
    defaultDiskQuotaPolicy: quotaPolicy,
    defaultDiskQuota: quota,
  })
}

watch(quotaPolicy, () => {
  if (quotaPolicy.value === 'UNLIMITED') quota.value = null
})
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.configuration.general.default_disk_quota.title')"
    :description="t('pages.configuration.general.default_disk_quota.description')"
    :error="isError"
  >
    <div class="flex flex-col gap-2">
      <PDropdown
        :options="options"
        option-label="name"
        option-value="value"
        class="w-full md:w-14rem"
        :class="{ 'p-invalid': isError }"
        :model-value="quotaPolicy"
        @update:model-value="val => patch(val, undefined)"
      >
        <template #value="{ value: selectedOption }">
          <div v-if="selectedOption" class="flex items-center gap-2">
            <i :class="getOption(selectedOption)!.icon" />
            <div class="text-truncate">
              {{ getOption(selectedOption)!.name }}
            </div>
          </div>
          <span v-else>
            {{ t('pages.configuration.general.default_disk_quota.choose') }}
          </span>
        </template>
        <template #option="slotProps">
          <div class="flex items-center gap-2">
            <i :class="slotProps.option.icon" />
            <div>{{ slotProps.option.name }}</div>
          </div>
        </template>
      </PDropdown>

      <FileSizeInput
        class="w-full"
        :class="{ 'p-invalid': isError }"
        :disabled="quotaPolicy === 'UNLIMITED'"
        :placeholder="inputPlaceholder"
        :model-value="quota"
        @update:model-value="val => patch(undefined, val)"
      />
    </div>
  </HorizontalActionPanel>
</template>
