<script lang="ts" setup>
const { t } = useI18n()
const { user } = storeToRefs(useAuth())

const isAdmin = computed(() => user.value?.role === 'ADMIN' || user.value?.role === 'OWNER')
</script>

<template>
  <footer class="bottom-bar grid overflow-hidden gap-4 h-5em w-full">
    <div class="btn">
      <BarButton
        icon="i-tabler-file" :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
      />
    </div>
    <div class="btn">
      <BarButton
        icon="i-tabler-star" :active="route => route.startsWith('/favorites')" @click="navigateTo('/favorites')"
      />
    </div>
    <div />
    <div class="btn">
      <BarButton
        icon="i-tabler-upload" :active="route => route.startsWith('/upload')" @click="navigateTo('/upload')"
      />
    </div>
    <div class="btn">
      <BarButton
        icon="i-tabler-timeline" :active="route => route.startsWith('/analytics')" @click="navigateTo('/analytics')"
      />
    </div>

    <div class="menu-btn">
      <BarButton icon="i-tabler-chevrons-up" :active="route => false" />
    </div>
  </footer>
</template>

<style scoped lang="scss">
.bottom-bar {
  background-color: var(--primary-500);
  color: var(--primary-color-text);
  grid-template-columns: repeat(5, 1fr);

  .dark & {
    background-color: var(--primary-800);

  }

  > div.btn {
    padding: 1em 0;
    margin: auto;
  }

  .menu-btn {
    position: absolute;
    left: 50%;
    transform: translateX(-50%) translateY(-50%);
    outline: .75em solid var(--ground);
    background-color: var(--primary-700);
    border-radius: 50%;

    > button {
      font-size: 1rem;
      border-radius: 50%;
      align-items: center;
      justify-content: center;

      &.active {
        background-color: transparent;
        color: var(--primary-color-text);
      }
    }
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.15s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.v-enter-active,
.v-leave-active {
  transition: opacity .3s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
