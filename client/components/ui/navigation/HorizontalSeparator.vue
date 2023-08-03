<template>
  <div class="px-4 w-full">
    <div class="sep" />
  </div>
</template>

<style scoped lang="scss">
.sep {
  width: 100%;
  border-bottom: 1px solid var(--primary-400);

  .dark & {
    border-bottom: 1px solid var(--primary-700);
  }
}
</style>
