<script setup lang="ts">
export interface QuotaPreviewerProps {
  quota: number
  max: number
  ratio: number
}

const { quota, max, ratio } = defineProps<QuotaPreviewerProps>()
const { t } = useI18n()
const { format } = useHumanFileSize()

const quotaFormat = computed(() => format(quota))
const maxFormat = computed(() => format(max))
const unlimited = computed(() => max === -1)
const severity = computed(() => {
  if (unlimited.value) return '--green-500'
  if (ratio >= 0.9) return '--red-500'
  if (ratio >= 0.8) return '--orange-500'
  return '--primary-500'
})
</script>

<template>
  <div class="flex-center flex-col gap-1.5 h-full">
    <PProgressBar
      class="max-h-2 w-full"
      :value="unlimited ? 100 : ratio * 100"
      :show-value="false"
      :pt="{ value: { style: { background: `var(${severity})` } } }"
    />
    <span v-if="!unlimited" class="quota-caption text-xs mb--1">{{ quotaFormat }} / {{ maxFormat }}</span>
    <span v-else class="quota-caption text-xs mb--1">{{ quotaFormat }} / {{ t('unlimited_quota') }}</span>
  </div>
</template>

<style scoped>
.quota-caption {
  min-width: 10rem;
  margin: 0 1em;
  text-wrap: nowrap;
  text-align: center;
}
</style>
