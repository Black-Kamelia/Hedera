<script lang="ts" setup>
const { t } = useI18n()
</script>

<template>
  <div class="flex flex-col gap-3 relative">
    <h1 class="text-2xl">
      {{ t('pages.profile.tokens.title') }}
    </h1>

    <PersonalTokensList />
  </div>
</template>
