<script setup lang="ts">
interface QuotaPreviewerProps {
  quota: number
  max: number
  unlimited?: boolean
}

const { quota, max, unlimited = false } = definePropsRefs<QuotaPreviewerProps>()
const { locale, t } = useI18n()

const quotaFormat = humanSize(quota.value, locale.value, t)
const maxFormat = humanSize(max.value, locale.value, t)
</script>

<template>
  <div class="flex flex-col justify-center items-center gap-1.5 h-full quota">
    <PProgressBar style="height: .5em; width: 100%" :value="unlimited ? 100 : quota / max * 100" :show-value="false" />
    <span v-if="!unlimited" class="text-xs" style="margin-bottom: -.25rem;">{{ quotaFormat }} / {{ maxFormat }}</span>
    <span v-else class="text-xs" style="margin-bottom: -.25rem;">{{ t('unlimited-quota') }}</span>
  </div>
</template>

<style scoped>
.quota {
  min-width: 12.5rem;
  border-left: 1px solid var(--surface-border);
  padding-left: 1.75rem;
  border-right: 1px solid var(--surface-border);
  padding-right: 1.75rem;
}
</style>
