<script setup lang="ts">
useTheme()
</script>

<template>
  <div font="text" class="p-input-filled">
    <PToast />
    <NuxtLoadingIndicator />
    <NuxtLayout>
      <NuxtPage />
    </NuxtLayout>
  </div>
</template>
