<script lang="ts" setup>
const color = useColorMode()
</script>

<template>
  <div class="flex items-center justify-center w-screen h-screen">
    <div class="background" :class="color.value" />

    <slot />
  </div>
</template>

<style scoped lang="scss">
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

<style>
.card-slide-left-enter-active,
.card-slide-left-leave-active,
.card-slide-right-enter-active,
.card-slide-right-leave-active {
  transition: transform .6s cubic-bezier(0.87, 0, 0.13, 1);
  position: absolute;
}

.card-slide-left-enter-from,
.card-slide-right-leave-to {
  transform: translateX(100vw);
}

.card-slide-left-leave-to,
.card-slide-right-enter-from {
  transform: translateX(-100vw);
}
</style>
