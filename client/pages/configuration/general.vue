<script setup lang="ts">
const config = useGlobalConfiguration()
const { t } = useI18n()

const toggleRegistrations = computed(() => config.toggleRegistrations)
const defaultDiskQuotaPolicy = computed(() => config.defaultDiskQuotaPolicy)
const defaultDiskQuota = computed(() => config.defaultDiskQuota)
const maximumThumbnailCount = computed(() => config.maximumThumbnailCount)
</script>

<template>
  <div class="flex flex-col gap-3">
    <h1 class="text-2xl">
      {{ t('pages.configuration.general.headers.registrations') }}
    </h1>

    <EnableRegistration :value="toggleRegistrations" />
    <DefaultDiskQuota :quota-policy-value="defaultDiskQuotaPolicy" :quota-value="defaultDiskQuota" />

    <h1 class="text-2xl mt-6">
      {{ t('pages.configuration.general.headers.files') }}
    </h1>

    <MaximumThumbnailCount :value="maximumThumbnailCount" />

    <div class="mt-8" />
  </div>
</template>
