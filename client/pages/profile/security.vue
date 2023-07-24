<script setup lang="ts">
const { t } = useI18n()
</script>

<template>
  <div class="flex flex-col gap-3">
    <!-- <div class="flex flex-col lg:flex-row lg:flex-grow gap-3"> -->
    <PasswordUpdater />
    <!-- </div> -->
  </div>

  <!-- <div class="p-card p-5">
    <PMessage
      severity="warn" :closable="false" :pt="{
        root: { style: { margin: 0 } },
      }"
    >
      <template #messageicon>
        <i class="i-tabler-shield-lock-filled text-3xl mr-5" />
      </template>
      <h2 class="text-xl">
        Certains paramètres de sécurité requièrent votre attention
      </h2>
    </PMessage>
  </div> -->
</template>
