<script lang="ts" setup>
const { visibility } = defineProps<{
  visibility: string
}>()
const { t } = useI18n()
</script>

<template>
  <div v-if="visibility === 'PUBLIC'" class="flex flex-row items-center gap-2">
    <i class="i-tabler-world" />
    <span>{{ t('pages.files.visibility.public') }}</span>
  </div>
  <div v-else-if="visibility === 'UNLISTED'" class="flex flex-row items-center gap-2">
    <i class="i-tabler-link" />
    <span>{{ t('pages.files.visibility.unlisted') }}</span>
  </div>
  <div v-else-if="visibility === 'PROTECTED'" class="flex flex-row items-center gap-2">
    <i class="i-tabler-lock" />
    <span>{{ t('pages.files.visibility.protected') }}</span>
  </div>
  <div v-else-if="visibility === 'PRIVATE'" class="flex flex-row items-center gap-2">
    <i class="i-tabler-eye-off" />
    <span>{{ t('pages.files.visibility.private') }}</span>
  </div>
  <div v-else class="flex flex-row items-center gap-2">
    <i class="i-tabler-help-triangle-filled" />
    <span>{{ t('pages.files.visibility.unknown') }}</span>
  </div>
</template>
