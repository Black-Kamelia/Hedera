<script lang="ts" setup>
const { t } = useI18n()
const { user } = storeToRefs(useAuth())

const isAdmin = computed(() => user.value?.role === 'ADMIN' || user.value?.role === 'OWNER')
</script>

<template>
  <footer class="bottom-bar overflow-hidden gap-4 px-4 h-5em w-full">
    <div class="main-btn relative">
      <BarButton icon="i-tabler-menu-2" :active="route => false" />
    </div>
    <div class="btn">
      <BarButton
        icon="i-tabler-file" :active="route => route.startsWith('/files')" @click="navigateTo('/files')"
      />
    </div>
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
    <div class="btn">
      <BarButton
        icon="i-tabler-user-circle" :active="route => route.startsWith('/profile')" @click="navigateTo('/profile')"
      />
    </div>
  </footer>
</template>

<style scoped lang="scss">
.main-btn {
  position: relative;
  width: 3em;

  > button {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translateX(-70%) translateY(-50%);
    background-color: var(--primary-600);
    font-size: 1.25rem;
    width: 5.625em;
    height: 5.625em;
    padding: 0 1.25em;
    align-items: center;
    justify-content: flex-end;
    border-radius: 50%;

    .dark & {
      background-color: var(--primary-700);
    }
  }
}

.bottom-bar {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  background-color: var(--primary-500);
  color: var(--primary-color-text);
  overflow-x: auto;

  .dark & {
    background-color: var(--primary-800);
  }

  > div.btn {
    padding: 1em 0;
    margin: auto;
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
