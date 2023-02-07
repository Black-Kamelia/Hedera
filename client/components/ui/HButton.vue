<script setup lang="ts">
import { cva } from 'class-variance-authority'

export interface HButtonProps {
  type?: 'primary' | 'secondary'
  variant?: 'solid' | 'flat' | 'text' | 'link'
  color?: 'primary' | 'success' | 'warning' | 'error' | 'info' | 'light' | 'dark'
  size?: 'sm' | 'md' | 'lg' | 'xl'
  disabled?: boolean
  leftIcon?: HIconClassName
  rightIcon?: HIconClassName
}

const props = withDefaults(defineProps<HButtonProps>(), {
  type: 'primary',
  variant: 'solid',
  color: 'primary',
  size: 'md',
  disabled: false,
  leftIcon: undefined,
  rightIcon: undefined,
})

const emit = defineEmits<SE<{
  click(e: MouseEvent): void
}>>()

const useStyle = cva(['font-600', 'active:scale-97.5', 'transition-100'], {
  variants: {
    color: {
      primary: ['bg-blue-500 text-white hover:bg-blue-600'],
      success: ['bg-green-500 text-white hover:bg-green-600'],
      warning: ['bg-yellow-400 text-white hover:bg-yellow-500'],
      error: ['bg-red-500 text-white hover:bg-red-600'],
      info: ['bg-cyan-500 text-white hover:bg-cyan-600'],
      light: ['bg-gray-100 text-black hover:bg-gray-300'],
      dark: ['bg-neutral-900 text-white hover:bg-neutral-700'],
    },
    type: {
      primary: [''],
      secondary: ['bg-white'],
    },
    size: {
      sm: ['text-sm px-2 py-.5 rounded-md'],
      md: ['px-3 py-1 rounded-lg'],
      lg: ['text-lg px-4 py-1 rounded-lg'],
      xl: ['text-xl px-5 py-2 rounded-xl'],
    },
  },
  compoundVariants: [
    {
      type: 'primary',
      color: 'dark',
      class: 'bg-neutral-800 text-white hover:bg-neutral-900',
    },
    {
      type: 'secondary',
      color: 'dark',
      class: 'bg-gray-100 text-black hover:bg-gray-300',
    },
  ],
})

const styles = useStyle({
  color: props.color,
  size: props.size,
})
</script>

<template>
  <button :class="styles" @click="emit('click', $event)">
    <div class="display-flex flex-row items-center">
      <span v-if="leftIcon !== undefined" :class="leftIcon" class="me-2" />
      <span><slot /></span>
      <span v-if="rightIcon !== undefined" :class="rightIcon" class="ms-2" />
    </div>
  </button>
</template>

<style scoped lang="scss">
</style>
