<script setup lang="ts">
export interface QuotaPreviewerProps {
  quota: FileSize
  max: FileSize
  ratio: number
  unlimited?: boolean
}

const { quota, max, ratio, unlimited = false } = defineProps<QuotaPreviewerProps>()
const { t } = useI18n()
const { format } = useHumanFileSize()

const quotaFormat = computed(() => format(quota))
const maxFormat = computed(() => format(max))
const severity = computed(() => {
  if (unlimited) return '--green-500'
  if (ratio >= 0.9) return '--red-500'
  if (ratio >= 0.8) return '--orange-500'
  return '--primary-500'
})
</script>

<template>
  <div class="flex-center flex-col gap-1.5 h-full quota">
    <PProgressBar
      class="max-h-2 w-full"
      :value="unlimited ? 100 : ratio * 100"
      :show-value="false"
      :pt="{ value: { style: { background: `var(${severity})` } } }"
    />
    <span v-if="!unlimited" class="text-xs mb--1">{{ quotaFormat }} / {{ maxFormat }}</span>
    <span v-else class="text-xs mb--1">{{ t('unlimited_quota') }}</span>
  </div>
</template>

<style scoped>
.quota {
  min-width: 10rem;
}
</style>
