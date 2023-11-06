<script setup lang="ts">
const config = storeToRefs(useGlobalConfiguration())
const { t } = useI18n()

const toggleRegistrations = computed(() => config.toggleRegistrations.value)
const defaultDiskQuotaPolicy = computed(() => config.defaultDiskQuotaPolicy.value)
const defaultDiskQuota = computed(() => config.defaultDiskQuota.value)
</script>

<template>
  <div class="flex flex-col gap-3">
    <h1 class="text-2xl">
      {{ t('pages.configuration.general.headers.registrations') }}
    </h1>

    <EnableRegistration :value="toggleRegistrations" />
    <DefaultDiskQuota :quota-policy-value="defaultDiskQuotaPolicy" :quota-value="defaultDiskQuota" />
  </div>
</template>
