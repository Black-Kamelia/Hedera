<script lang="ts" setup>
export interface TopBarProps {
  narrow?: boolean
}

const {
  narrow,
} = defineProps<TopBarProps>()

const { logout } = useAuth()
const pageName = usePageName()
</script>

<template>
  <header
    class="top-bar flex items-center overflow-hidden gap-4 py-5 h-5em w-full"
    :class="{ 'px-8': !narrow, 'px-4': narrow }"
  >
    <h2 class="text-3xl w-full text-ellipsis overflow-hidden whitespace-nowrap">
      {{ pageName }}
    </h2>

    <div v-if="!narrow" class="flex flex-row items-center gap-8 h-full">
      <QuotaPreviewer :quota="{ value: 10, shift: 20 }" :max="{ value: 20, shift: 20 }" :ratio="0.5" />
      <div class="sep" />
      <div class="flex flex-row gap-2">
        <ThemeSwitcher />
        <PButton
          v-tooltip.bottom="{ value: 'Log out', showDelay: '1000' }" icon="i-tabler-logout" text rounded
          @click="logout()"
        />
      </div>
    </div>
  </header>
</template>

<style scoped>
.top-bar {
  border-bottom: 1px solid var(--surface-border);
  background-color: var(--surface-overlay);

  .sep {
    height: 100%;
    border-left: 1px solid var(--surface-border);
  }
}
</style>
