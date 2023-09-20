<template>
  <div class="py-4 h-full">
    <div class="sep" />
  </div>
</template>

<style scoped lang="scss">
.sep {
  height: 100%;
  border-left: 1px solid var(--primary-400);

  .dark & {
    border-left: 1px solid var(--primary-700);
  }
}
</style>
