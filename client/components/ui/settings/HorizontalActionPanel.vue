<script lang="ts" setup>
const { header, description, localSetting = false } = defineProps<{
  header: string
  description: string
  localSetting?: boolean
  error?: boolean
}>()

const { t } = useI18n()
</script>

<template>
  <div class="p-card p-7 p-invalid">
    <div class="flex flex-row justify-between flex-wrap">
      <div class="flex flex-row items-center flex-wrap">
        <h2 class="text-lg font-bold mr-3">
          {{ header }}
        </h2>
        <PInlineMessage v-if="localSetting" class="px-2 py-1" severity="warn">
          <template #icon>
            <i class="i-tabler-alert-circle mr-2 w-1em h-1em" />
          </template>
          {{ t('pages.profile.settings.local_setting') }}
        </PInlineMessage>
      </div>
      <div v-if="error" class="flex items-center gap-1 text-[--red-400]">
        <i class="h-75% i-tabler-alert-square-filled" />
        {{ t('pages.profile.settings.sync_failed') }}
      </div>
    </div>
    <div class="flex items-start flex-col gap-4 md:flex-row md:gap-12 mt-2">
      <div class="flex items-start flex-grow flex-col gap-2">
        <p class="flex-grow p-text-secondary">
          {{ description }}
        </p>
        <slot name="cta" />
      </div>
      <div>
        <slot />
      </div>
    </div>
  </div>
</template>

<style scoped>
.p-inline-message {
  padding: 0.25rem 0.5rem;
}

.p-inline-message-text {
  font-size: 14px;
}
</style>
