<script lang="ts" setup>
const { status } = defineProps<{
  status: boolean
}>()
const { t } = useI18n()

const content = computed(() => status
  ? {
      icon: 'i-tabler-circle-check',
      text: t('pages.configuration.users.status.enabled'),
      color: 'text-[--green-600]',
    }
  : {
      icon: 'i-tabler-circle-x',
      text: t('pages.configuration.users.status.disabled'),
      color: 'text-[--red-600]',
    },
)
</script>

<template>
  <div class="flex flex-row items-center gap-2" :class="content.color">
    <i :class="content.icon" />
    <span>{{ content.text }}</span>
  </div>
</template>
