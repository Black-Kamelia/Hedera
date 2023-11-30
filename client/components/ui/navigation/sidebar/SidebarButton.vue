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
  <PButton rounded class="flex flex-row gap-4 items-center" :class="{ active: activeRoute, open }">
    <div class="flex">
      <slot name="icon">
        <i :class="icon" />
      </slot>
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

<style scoped lang="scss">
.p-button {
  background-color: transparent;
  color: var(--primary-color-text);
  padding: .75rem;
  transition: background-color .2s, color .2s, border-color .2s, box-shadow .2s, padding .3s ease;

  &.open {
    padding: .75rem 1.25rem;
  }

  &:enabled:hover {
    background-color: rgba(29, 30, 39, 0.1);

    .dark & {
      background-color: rgba(232, 233, 233, 0.05);
    }
  }

  &:enabled:active {
    background-color: rgba(29, 30, 39, 0.2);

    .dark & {
      background-color: rgba(232, 233, 233, 0.15);
    }
  }

  &.active {
    background-color: var(--primary-color-text);
    color: var(--primary-500);

    .dark & {
      color: var(--primary-800);
    }

    &:enabled:hover {
      background-color: var(--primary-color-text);
      color: var(--primary-500);

      .dark & {
        color: var(--primary-800);
      }
    }

    &:enabled:active {
      background-color: var(--primary-color-text);
    }
  }

  &,
  &:enabled:focus,
  &:enabled:hover,
  &:enabled:active {
    border: none;
    outline: none;
    box-shadow: none;
  }
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
