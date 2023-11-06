<script setup lang="ts">
const {
  quotaPolicyValue,
  quotaValue,
} = defineProps<{
  quotaPolicyValue: DiskQuotaPolicy
  quotaValue: number
}>()
const quotaPolicy = ref(quotaPolicyValue)
const quota = ref<Nullable<number>>(quotaValue)

const { t } = useI18n()
const { patchConfiguration: patchPolicy, isError: policyError } = useConfigurationField(quotaPolicy, value => ({ defaultDiskQuotaPolicy: value }))
const { patchConfiguration: patchQuota, isError: quotaError } = useConfigurationField(quota, value => ({ defaultDiskQuota: value }))

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

function patch() {
  if (quotaPolicy.value === 'UNLIMITED') {
    patchPolicy(quotaPolicy.value)
    patchQuota(null)
  } else if (quota.value !== null) {
    patchPolicy(quotaPolicy.value)
    patchQuota(quota.value)
  }
}

watch(quotaPolicy, () => {
  if (quotaPolicy.value === 'UNLIMITED') quota.value = null
})
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.configuration.general.default_disk_quota.title')"
    :description="t('pages.configuration.general.default_disk_quota.description')"
    :error="policyError || quotaError"
  >
    <div class="flex flex-col gap-2">
      <PDropdown
        v-model="quotaPolicy"
        :options="options"
        option-label="name"
        option-value="value"
        class="w-full md:w-14rem"
        :class="{ 'p-invalid': policyError }"
        @update:model-value="patch"
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
        v-model="quota"
        class="w-full"
        :class="{ 'p-invalid': quotaError }"
        :disabled="quotaPolicy === 'UNLIMITED'"
        :placeholder="inputPlaceholder"
        @update:model-value="patch"
      />
    </div>
  </HorizontalActionPanel>
</template>
