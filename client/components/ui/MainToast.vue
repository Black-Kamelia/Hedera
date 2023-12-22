<script lang="ts" setup>
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
      transition: {
        enterFromClass: 'toast-enter-from',
        enterActiveClass: 'toast-enter-active',
        leaveActiveClass: 'toast-leave-active',
        leaveToClass: 'toast-leave-to',
      },
    }"
  >
    <template #message="{ message: { detail, severity, summary } }">
      <div class="content">
        <i :class="getIcon(severity, detail)" class="text-xl" />
        <span class="font-bold">
          {{ summary }}
        </span>
        <span v-if="detail?.text" class="details text-sm">
          {{ detail!.text }}
        </span>
      </div>
    </template>
    <template #closeicon>
      <i class="i-tabler-x" />
    </template>
  </PToast>
</template>

<style scoped>
.content {
  flex-grow: 1;
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 0.25rem 1em;
  align-items: center;

  .details {
    grid-column-start: 2;
    grid-column-end: 3;
  }
}
</style>

<style>
.toast-enter-active,
.toast-leave-active{
  transition: opacity 0.3s ease, transform 0.4s cubic-bezier(0.25, 1, 0.5, 1);
}

.toast-enter-from {
  opacity: 0;
  transform: translateY(150%);
}
.toast-leave-to {
  opacity: 0;
}
</style>
