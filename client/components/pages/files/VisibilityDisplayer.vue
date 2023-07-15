<script lang="ts" setup>
import type { FileVisibility } from '~/composables/fileTable/useChangeFileVisibility'

const { visibility } = defineProps<{
  visibility: FileVisibility
}>()
const { t } = useI18n()

const mapper = computed(() => ({
  PUBLIC: { icon: 'i-tabler-world', text: t('pages.files.visibility.public') },
  UNLISTED: { icon: 'i-tabler-link', text: t('pages.files.visibility.unlisted') },
  PROTECTED: { icon: 'i-tabler-lock', text: t('pages.files.visibility.protected') },
  PRIVATE: { icon: 'i-tabler-eye-off', text: t('pages.files.visibility.private') },
  UNKNOWN: { icon: 'i-tabler-help-triangle-filled', text: t('pages.files.visibility.unknown') },
}))
</script>

<template>
  <div class="flex flex-row items-center gap-2">
    <i :class="mapper[visibility].icon" />
    <span>{{ mapper[visibility].text }}</span>
  </div>
</template>
