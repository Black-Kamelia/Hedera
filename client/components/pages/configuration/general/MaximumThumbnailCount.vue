<script setup lang="ts">
const { value: initialValue } = defineProps<{
  value: number
}>()
const model = ref(initialValue)

const { t } = useI18n()
const { patchConfiguration, isError } = useConfigurationField(model, value => ({ maximumThumbnailCount: value }))

watchDebounced(model, (val) => {
  patchConfiguration(val)
}, { debounce: 500 })
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.configuration.general.maximum_thumbnail_count.title')"
    :description="t('pages.configuration.general.maximum_thumbnail_count.description')"
    :error="isError"
  >
    <PInputNumber v-model="model" :class="{ 'p-invalid': isError }" />

    <template #cta>
      <p class="p-text-secondary">
        {{ t('pages.configuration.general.maximum_thumbnail_count.advice') }}
      </p>
    </template>

    <!--
    <template #cta>
      <i18n-t
        keypath="pages.configuration.general.maximum_thumbnail_count.cache_size"
        tag="div"
        scope="global"
        class="flex flew-row gap-1 items-center"
      >
        <template #size>
          <PSkeleton height="1rem" width="5rem" />
        </template>
      </i18n-t>
    </template>
    -->
  </HorizontalActionPanel>
</template>
