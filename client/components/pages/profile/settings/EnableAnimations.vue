<script setup lang="ts">
const model = defineModel<boolean>({ required: true })
const { t } = useI18n()
</script>

<template>
  <HorizontalActionPanel
    :header="t('pages.profile.settings.enable_animations.title')"
    :description="t('pages.profile.settings.enable_animations.description')"
    local-setting
  >
    <template #header-ornement>
      <PInlineMessage class="px-2 py-1" severity="warn">
        <template #icon>
          <i class="i-tabler-alert-circle mr-2 w-1em h-1em" />
        </template>
        Pour ce navigateur uniquement
      </PInlineMessage>
    </template>
    <PInputSwitch v-model="model" />
  </HorizontalActionPanel>
</template>

<style scoped>
.p-inline-message {
  padding: 0.25rem 0.5rem;
}

.p-inline-message-text {
  font-size: 14px;
}
</style>
