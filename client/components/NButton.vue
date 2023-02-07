<script setup lang="ts">
</script>

<template>
  <button class="bg-blue-5 text-white px-3 py-1 pill display-block m-auto hover:bg-blue-4 transition-75">
    <slot />
  </button>
</template>

<style scoped>
.pill {
  border-radius: 1rem;
}

button:active {
  transform: scale(0.975);
}
</style>
