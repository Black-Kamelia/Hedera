<script lang="ts" setup>
export interface TopBarProps {
  narrow?: boolean
}

const { narrow } = defineProps<TopBarProps>()

const pageName = usePageName()

const auth = useAuth()
const { logout } = auth
const { user } = storeToRefs(auth)
</script>

<template>
  <header
    class="top-bar flex items-center overflow-hidden gap-4 py-5 h-5em w-full"
    :class="{ 'px-8': !narrow, 'px-4': narrow }"
  >
    <h2 class="text-3xl w-full truncate">
      {{ pageName }}
    </h2>

    <div v-if="!narrow" class="flex flex-row items-center gap-8 h-full">
      <QuotaPreviewer
        v-if="user"
        :quota="user.currentDiskQuota"
        :max="user.maximumDiskQuota"
        :ratio="user.currentDiskQuotaRatio"
      />
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
