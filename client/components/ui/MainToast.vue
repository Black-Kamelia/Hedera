<script lang="ts" setup>
const toast = useToast()
</script>

<template>
  <PToast
    position="bottom-right"
    :pt="{
      buttonContainer: { class: 'display-none' },
      container: { class: 'inline-block' },
      message: { class: 'flex flex-col items-end' },
    }"
    @click="toast.removeAllGroups()"
  >
    <template #message="{ message: { detail, severity, summary } }">
      <div class="flex gap-2 pr-1" :class="{ 'items-center': !detail?.text }">
        <i v-if="detail?.icon" :class="detail!.icon" />
        <i v-else-if="severity === 'success'" class="i-tabler-circle-check-filled" />
        <i v-else-if="severity === 'info'" class="i-tabler-info-circle-filled" />
        <i v-else-if="severity === 'warn'" class="i-tabler-alert-triangle-filled" />
        <i v-else-if="severity === 'error'" class="i-tabler-alert-circle-filled" />
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
