<template>
  <div class="main ground overflow-hidden" w-screen>
    <Topbar narrow />
    <div class="overflow-auto">
      <slot />
    </div>
    <BottomBar />
  </div>

  <MainToast />
</template>

<style lang="scss">
.main {
  height: 100dvh;
  display: grid;
  grid-template-rows: auto 1fr auto;
}

.content {
  transition: max-width 0.3s cubic-bezier(0.25, 1, 0.5, 1), width 0.3s cubic-bezier(0.25, 1, 0.5, 1);

  &.open {
    max-width: calc(100% - var(--sidebar-width-open));
  }

  &.closed {
    max-width: calc(100% - var(--sidebar-width-collapsed));
  }
}
</style>
