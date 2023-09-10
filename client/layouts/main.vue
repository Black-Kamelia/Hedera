<script lang="ts" setup>
const isNarrow = useMediaQuery('(max-width: 640px)')
</script>

<template>
  <div
    class="ground overflow-hidden w-screen"
    :class="{ desktop: !isNarrow, mobile: isNarrow }"
  >
    <Sidebar v-if="!isNarrow" />
    <div class="content" :class="{ 'overflow-auto': isNarrow }">
      <Topbar :narrow="isNarrow" />
      <div class="h-full" :class="{ 'overflow-auto': !isNarrow }">
        <slot />
      </div>
    </div>
    <BottomBar v-if="isNarrow" />
    <MainToast />
  </div>
</template>

<style lang="scss">
.desktop {
  height: 100dvh;
  display: grid;
  grid-template-columns: auto 1fr;

  > .content {
    height: 100dvh;
    display: grid;
    grid-template-rows: auto 1fr;
  }
}

.mobile {
  height: 100dvh;
  display: grid;
  grid-template-rows: 1fr auto;
}
</style>
