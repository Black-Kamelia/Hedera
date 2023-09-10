<script setup lang="ts">
export interface SidebarButtonProps {
  open: boolean
  icon: string
  endIcon?: string
  label: string
  active?: (route: any) => boolean
}

const {
  open,
  icon,
  endIcon = undefined,
  label,
  active,
} = defineProps<SidebarButtonProps>()

const router = useRouter()

const activeRoute = computed(() => active?.(router.currentRoute.value.fullPath) ?? false)
</script>

<template>
  <PButton rounded class="flex flex-row gap-4 items-start" :class="{ active: activeRoute, open }">
    <div class="flex">
      <i :class="icon" />
    </div>
    <Transition>
      <div v-show="open" class="flex flex-row gap-1 justify-between items-center flex-grow overflow-hidden">
        <span class="overflow-hidden text-left whitespace-nowrap">
          {{ label }}
        </span>
        <i v-if="endIcon" class="text-3 opacity-50%" :class="endIcon" />
      </div>
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
