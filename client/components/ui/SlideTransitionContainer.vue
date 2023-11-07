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
      :name="`slide-${direction.toLowerCase()}`" @after-enter="cardTransition = false"
      @before-enter="cardTransition = true"
    >
      <slot />
    </Transition>
  </MorphAnimatedContainer>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all .5s cubic-bezier(0.65, 0, 0.35, 1);
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
