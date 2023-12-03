<script lang="ts" setup>
const toast = useToast()

function getIcon(severity: string, detail?: { icon: string }) {
  if (detail?.icon) return detail.icon
  if (severity === 'success') return 'i-tabler-circle-check-filled'
  if (severity === 'info') return 'i-tabler-info-circle-filled'
  if (severity === 'warn') return 'i-tabler-alert-triangle-filled'
  return 'i-tabler-alert-circle-filled'
}
</script>

<template>
  <PToast
    position="bottom-right"
    :pt="{
      container: { class: 'inline-block' },
      message: { class: 'flex flex-col items-end' },
    }"
  >
    <template #message="{ message: { detail, severity, summary } }">
      <div class="flex gap-2 pr-1" :class="{ 'items-center': !detail?.text }">
        <i :class="getIcon(severity, detail)" />
        <div class="flex flex-col gap-1">
          <span class="font-bold">
            {{ summary }}
          </span>
          <span v-if="detail?.text" class="text-sm">
            {{ detail!.text }}
          </span>
        </div>
      </div>
    </template>
  </PToast>
</template>
