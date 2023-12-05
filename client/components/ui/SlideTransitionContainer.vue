<script lang="ts" setup>
export type SlideDirection = 'LEFT' | 'RIGHT'

const {
  gap = '0px',
  direction = 'LEFT',
  animateHeight = true,
  animateWidth = false,
} = defineProps<{
  gap?: string
  direction?: SlideDirection
  animateHeight?: boolean
  animateWidth?: boolean
}>()

const cardTransition = ref(false)
</script>

<template>
  <MorphAnimatedContainer
    :prevent-transition="!cardTransition"
    :animate-height="animateHeight"
    :animate-width="animateWidth"
  >
    <Transition
      :name="`slide-${direction.toLowerCase()}`"
      @after-enter="cardTransition = false"
      @before-enter="cardTransition = true"
    >
      <slot />
    </Transition>
  </MorphAnimatedContainer>
</template>

<style scoped lang="scss">
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all 0.4s cubic-bezier(0.76, 0, 0.24, 1);
}

.slide-left-leave-active,
.slide-right-leave-active {
  position: absolute;
  top: 0;
}

.slide-left-leave-to,
.slide-right-enter-from {
  opacity: 0;
  transform: translateX(calc(-100% - v-bind(gap)));
}

.slide-left-enter-from,
.slide-right-leave-to {
  opacity: 0;
  transform: translateX(calc(100% + v-bind(gap)));
}
</style>
