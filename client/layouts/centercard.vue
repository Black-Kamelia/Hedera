<script lang="ts" setup>
const color = useColorMode()
</script>

<template>
  <div class="flex items-center justify-center w-screen h-screen p-10">
    <div class="background" :class="color.value" />

    <div class="p-card main-card overflow-x-hidden scroll max-h-full">
      <slot />
    </div>
  </div>
</template>

<style scoped lang="scss">
.scroll:not(:has(.slide-left-enter-active)) {
  overflow-y: auto;
}

.background {
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  height: 100%;
  left: 0;
  position: fixed;
  right: 0;
  top: 0;
  z-index: -1;

  &.light {
    background-image: url("/assets/img/06-Blue_Purple_LM-4K.png");
  }

  &.dark {
    background-image: url("/assets/img/06-Blue_Purple_DM-4K.png");
  }
}
</style>
