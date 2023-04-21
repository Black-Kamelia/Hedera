<script setup lang="ts">
export interface SidebarButtonProps {
  open: boolean
  icon: string
  label: string
  active?: (route: any) => boolean
}

const {
  open,
  icon,
  label,
  active,
} = definePropsRefs<SidebarButtonProps>()

const router = useRouter()

const activeRoute = computed(() => active.value?.(router.currentRoute.value.fullPath) ?? false)
</script>

<template>
  <PButton rounded class="flex flex-row gap-4 items-start" :class="{ active: activeRoute, open }">
    <div class="flex">
      <span :class="icon" />
    </div>
    <Transition>
      <span v-show="open" class="flex-grow overflow-hidden text-left whitespace-nowrap">
        {{ label }}
      </span>
    </Transition>
  </PButton>
</template>

<style scoped>
.v-enter-active,
.v-leave-active {
  transition: opacity .3s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
