<script setup lang="ts">
export interface QuotaPreviewerProps {
  quota: number
  max: number
  unlimited?: boolean
}

const { quota, max, unlimited = false } = $defineProps<QuotaPreviewerProps>()
const { locale, t } = useI18n()

const quotaFormat = computed(() => humanSize(quota, locale.value, t))
const maxFormat = computed(() => humanSize(max, locale.value, t))
</script>

<template>
  <div class="flex-center flex-col gap-1.5 h-full quota">
    <PProgressBar class="max-h-2 w-full" :value="unlimited ? 100 : quota / max * 100" :show-value="false" />
    <span v-if="!unlimited" class="text-xs mb--1">{{ quotaFormat }} / {{ maxFormat }}</span>
    <span v-else class="text-xs mb--1">{{ t('unlimited-quota') }}</span>
  </div>
</template>

<style scoped>
.quota {
  min-width: 10rem;
}
</style>
