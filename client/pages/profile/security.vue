<script lang="ts" setup>
const { t } = useI18n()
</script>

<template>
  <div class="flex flex-col gap-3 relative">
    <h1 class="text-2xl">
      {{ t('pages.profile.security.change_password') }}
    </h1>

    <div class="p-card p-7 w-full">
      <PasswordUpdater />
    </div>
  </div>
</template>
