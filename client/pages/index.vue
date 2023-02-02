<script lang="ts" setup>
const dark = $isDark
</script>

<template>
  <div center h-screen>
    <button class="icon-btn absolute top-10 right-10 text-8" @click="toggleDark()">
      <div i="tabler-sun dark:tabler-moon" />
    </button>
    <p class="absolute top-10 left-10 text-8 font-text">
      {{ dark ? 'Dark' : 'Light' }}
    </p>

    <h1 font="display bold" text="30 hover:violet-700" transition>
      Welcome to Hedera
    </h1>
  </div>
</template>
