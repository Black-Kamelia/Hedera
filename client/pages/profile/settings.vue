<script setup lang="ts">
const fileSizeScale = ref('BINARY')
const defaultFileVisibility = ref('UNLISTED')
const autoDeleteOldestFiles = ref(false)
const enableAnimations = ref(true)
const preferredTheme = ref('DARK')
const preferredLocale = ref('en')
const preferredDateTimeFormat = ref()

const animations = useLocalStorage('animations', true)
</script>

<template>
  <div class="flex flex-col gap-3">
    <h1 class="text-2xl">
      Envoi de fichiers
    </h1>
    <DefaultFileVisibility v-model="defaultFileVisibility" />
    <AutoDeleteOldestFiles v-model="autoDeleteOldestFiles" />

    <h1 class="text-2xl mt-6">
      Affichage et animations
    </h1>
    <FileSizeScale v-model="fileSizeScale" />
    <EnableAnimations v-model="animations" />
    <PreferredTheme v-model="preferredTheme" />
    <PreferredDateTimeFormat v-model="preferredDateTimeFormat" />
    <PreferredLocale v-model="preferredLocale" />

    <div class="flex items-center justify-center flex-col mt-8 p-4 color-[--text-color-secondary]">
      <div class="logo w-20em h-4em" />
      <p>Version 0.0.1</p>
      <small><a href="https://black-kamelia.com/">Black-Kamelia</a> • <a href="https://git.black-kamelia.com/Hedera">GitHub</a></small>
    </div>
  </div>
</template>

<style scoped>
.logo {
  background-color: var(--text-color-secondary);
  -webkit-mask: url(/assets/img/Hedera.svg) no-repeat center;
  mask: url(/assets/img/Hedera.svg) no-repeat center;
}
</style>
