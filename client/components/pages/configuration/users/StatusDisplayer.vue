<script lang="ts" setup>
const { status } = defineProps<{
  status: boolean
}>()
const { t } = useI18n()

const mapper = computed(() => ({
  true: {
    icon: 'i-tabler-circle-check',
    text: t('pages.configuration.users.status.enabled'),
    color: 'text-[--green-600]',
  },
  false: {
    icon: 'i-tabler-circle-x',
    text: t('pages.configuration.users.status.disabled'),
    color: 'text-[--red-600]',
  },
}))
</script>

<template>
  <div class="flex flex-row items-center gap-2" :class="mapper[status].color">
    <i :class="mapper[status].icon" />
    <span>{{ mapper[status].text }}</span>
  </div>
</template>
