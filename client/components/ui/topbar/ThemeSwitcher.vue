<script lang="ts" setup>
import type { PContextMenu } from '#components'

const { t } = useI18n()
const color = useColorMode()

const overlayPanel = ref<InstanceType<typeof PContextMenu> | null>()
const options = computed(() => [
  { icon: 'i-tabler-device-desktop', name: t('theme.system'), value: 'system' },
  { icon: 'i-tabler-sun', name: t('theme.light'), value: 'light' },
  { icon: 'i-tabler-moon', name: t('theme.dark'), value: 'dark' },
])

function openThemeSwitcher(event: Event) {
  overlayPanel.value?.toggle(event)
}

function onChange() {
  overlayPanel.value?.hide()
}
</script>

<template>
  <PButton v-tooltip.bottom="'Theme'" icon="i-tabler-palette" text rounded @click="openThemeSwitcher" />

  <POverlayPanel
    ref="overlayPanel"
    :pt="{
      content: {
        style: {
          padding: '0',
        },
      },
    }"
  >
    <PListbox
      v-model="color.preference"
      :pt="{
        root: {
          style: {
            border: 'none',
          },
        },
      }"
      :options="options"
      option-label="name"
      option-value="value"
      @change="onChange()"
      @update:model-value="(value) => color.preference = value ?? 'system'"
    >
      <template #option="option">
        <div class="flex flex-row items-center gap-2">
          <i :class="option.option.icon" />
          <span>{{ option.option.name }}</span>
        </div>
      </template>
    </PListbox>
  </POverlayPanel>
</template>

<style scoped>
.p-listbox.p-component.p-focus {
  box-shadow: none;
  border: none;
  outline: none;
}
</style>
