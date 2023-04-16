<script setup lang="ts">
useTheme()
</script>

<template>
  <div font="text" class="p-input-filled">
    <PToast close-icon="i-tabler-x" error-icon="i-tabler-alert-circle-filled" />
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
