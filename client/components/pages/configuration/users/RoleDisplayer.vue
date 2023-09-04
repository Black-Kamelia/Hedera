<script lang="ts" setup>
const { role } = defineProps<{
  role: Role
}>()
const { t } = useI18n()

const mapper = computed(() => ({
  REGULAR: { icon: 'i-tabler-user', text: t('pages.configuration.users.role.regular') },
  ADMIN: { icon: 'i-tabler-shield', text: t('pages.configuration.users.role.admin') },
  OWNER: { icon: 'i-tabler-shield-chevron', text: t('pages.configuration.users.role.owner') },
  UNKNOWN: { icon: 'i-tabler-help-triangle-filled', text: t('pages.configuration.users.role.unknown') },
}))
</script>

<template>
  <div class="flex flex-row items-center gap-2">
    <i :class="mapper[role].icon" />
    <span>{{ mapper[role].text }}</span>
  </div>
</template>
