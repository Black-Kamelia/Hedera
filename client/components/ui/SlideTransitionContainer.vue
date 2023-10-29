<script lang="ts" setup>
export type SlideDirection = 'LEFT' | 'RIGHT'

const { direction = 'LEFT' } = defineProps<{
  direction?: SlideDirection
}>()

const cardTransition = ref(false)
</script>

<template>
  <HeightAnimatedContainer :prevent-transition="!cardTransition">
    <Transition :name="`slide-${direction.toLowerCase()}`" @after-enter="cardTransition = false" @before-enter="cardTransition = true">
      <slot />
    </Transition>
  </HeightAnimatedContainer>
</template>

<style scoped>
.slide-left-enter-active,
.slide-left-leave-active,
.slide-right-enter-active,
.slide-right-leave-active {
  transition: all .4s cubic-bezier(0.87, 0, 0.13, 1);
  width: 100%;
}

.slide-left-leave-active,
.slide-right-leave-active {
  position: absolute;
  top: 0;
}

.slide-left-enter-from {
  opacity: 0;
  transform: translateX(100%);
}

.slide-left-leave-to {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-right-enter-from {
  opacity: 0;
  transform: translateX(-100%);
}

.slide-right-leave-to {
  opacity: 0;
  transform: translateX(100%);
}
</style>
