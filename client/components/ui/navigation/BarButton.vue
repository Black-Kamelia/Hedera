<script setup lang="ts">
export interface BarButtonProps {
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
} = defineProps<BarButtonProps>()

const router = useRouter()

const activeRoute = computed(() => active?.(router.currentRoute.value.fullPath) ?? false)
</script>

<template>
  <PButton
    rounded class="flex flex-row gap-4 items-start"
    :class="{ active: activeRoute, open }"
    v-bind="$attrs"
  >
    <div class="flex">
      <i :class="icon" />
    </div>
    <Transition>
      <span v-show="open" class="flex-grow overflow-hidden text-left whitespace-nowrap">
        {{ label }}
      </span>
    </Transition>
  </PButton>
</template>

<style scoped>
.p-button,
.p-button:active,
.p-button:focus,
.p-button:hover {
  border: none;
  outline: none;
  box-shadow: none;
}

.p-button {
  background-color: transparent;
  padding: .75rem;
  min-width: 3em;
  min-height: 3em;
  transition: background-color .2s, color .2s, border-color .2s, box-shadow .2s, padding 0.3s ease;

  &:hover {
    background-color: rgba(29, 30, 39, 0.1);
  }

  &:active {
    background-color: rgba(29, 30, 39, 0.2);
  }

  &.active {
    background-color: var(--primary-color-text);
    color: var(--primary-500);
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
