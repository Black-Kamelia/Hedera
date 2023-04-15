<script setup lang="ts">
interface SidebarButtonProps {
  open: boolean
  icon: string
  label: string
  active?: (route: any) => boolean
}

const props = defineProps<SidebarButtonProps>()

const router = useRouter()

const activeRoute = computed(() => {
  return props.active?.(router.currentRoute.value.fullPath)
})
</script>

<template>
  <PButton rounded class="flex flex-row gap-4 items-start" :class="{ active: activeRoute, open: props.open }">
    <div class="flex">
      <i :class="props.icon" />
    </div>
    <Transition>
      <span v-show="props.open" class="flex-grow overflow-hidden text-left whitespace-nowrap">
        {{ props.label }}
      </span>
    </Transition>
  </PButton>
</template>

<style scoped>
/*
.p-button {
    transition: border-radius 0.3s ease;
}
*/

.v-enter-active,
.v-leave-active {
  transition: opacity .3s ease;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
}
</style>
