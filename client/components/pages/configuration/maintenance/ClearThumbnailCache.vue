<script setup lang="ts">
const { t } = useI18n()
const { format } = useHumanFileSize()
const { data: cacheSize, pending: pendingCacheSize, refresh } = useFetchAPI<number>('/configuration/maintenance/thumbnail-cache-size')

const { execute, status } = useFetchAPI('/configuration/maintenance/clear-thumbnail-cache', { method: 'POST', immediate: false })

function clear() {
  execute().then(() => refresh())
}
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.configuration.maintenance.clear_thumbnail_cache.title')"
    :description="t('pages.configuration.maintenance.clear_thumbnail_cache.description')"
  >
    <PButton
      :label="t('pages.configuration.maintenance.clear_thumbnail_cache.clear_cache')"
      class="min-w-12em"
      :loading="status === 'pending'"
      @click="clear()"
    />

    <template #cta>
      <i18n-t
        keypath="pages.configuration.maintenance.clear_thumbnail_cache.cache_size"
        tag="div"
        scope="global"
        class="flex flew-row gap-1 items-center"
      >
        <template #size>
          <PSkeleton v-if="cacheSize === null || pendingCacheSize" height="1rem" width="5rem" />
          <span v-else>{{ format(cacheSize) }}</span>
        </template>
      </i18n-t>
    </template>
  </HorizontalActionPanel>
</template>
