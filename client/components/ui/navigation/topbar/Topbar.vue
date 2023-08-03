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
    class="top-bar flex items-center overflow-hidden gap-4 h-5em w-full"
    :class="{ 'px-8': !narrow, 'px-4': narrow }"
  >
    <h2 class="text-3xl w-full">
      {{ pageName }}
    </h2>
    <!-- <QuotaPreviewer :quota="{ value: 10, shift: 20 }" :max="{ value: 20, shift: 20 }" :ratio="0.5" /> -->
    <VerticalSeparator />
    <div class="flex flex-row gap-2">
      <!--
      <ThemeSwitcher />
      <PButton v-tooltip.bottom="{ value: 'Notification', showDelay: '1000' }" icon="i-tabler-bell" text rounded />
      -->
      <PButton
        v-tooltip.bottom="{ value: 'Log out', showDelay: '1000' }" icon="i-tabler-logout" text rounded
        @click="logout()"
      />
    </div>
  </header>

  <!--
  <header class="top-bar flex items-center overflow-hidden overflow-x-auto gap-4 px-8 py-5 h-20 w-full bg-[var(--surface-overlay)]">
    <h2 class="text-3xl w-full">
      {{ pageName }}
    </h2>
    <QuotaPreviewer :quota="{ value: 10, shift: 20 }" :max="{ value: 20, shift: 20 }" :ratio="0.5" />
    <TopbarSeparator />
    <div class="flex flex-row gap-2">
      <ThemeSwitcher />
      <PButton v-tooltip.bottom="{ value: 'Notification', showDelay: '1000' }" icon="i-tabler-bell" text rounded />
      <PButton v-tooltip.bottom="{ value: 'Log out', showDelay: '1000' }" icon="i-tabler-logout" text rounded @click="logout()" />
    </div>
  </header>
  -->
</template>

<style scoped>
.top-bar {
  border-bottom: 1px solid var(--surface-border);
  background-color: var(--surface-overlay);
}
</style>
