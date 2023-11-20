<script lang="ts" setup>
const {
  preventTransition = false,
  animateHeight = true,
  animateWidth = true,
  transitionDuration = '0.4s',
} = defineProps<{
  preventTransition?: boolean
  animateHeight?: boolean
  animateWidth?: boolean
  transitionDuration?: string
}>()

const child = ref<Nullable<HTMLElement>>(null)
const { width, height } = useElementSize(child)
</script>

<template>
  <div
    class="relative w-full"
    :class="{ 'morph-transition': !preventTransition }"
    :style="{
      width: animateWidth ? `${width}px` : 'auto',
      height: animateHeight ? `${height}px` : 'auto',
    }"
  >
    <div ref="child" :class="{ 'w-min': animateWidth, 'h-min': animateHeight }">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.morph-transition {
  transition:
    width v-bind(transitionDuration) cubic-bezier(0.76, 0, 0.24, 1),
    height v-bind(transitionDuration) cubic-bezier(0.76, 0, 0.24, 1);
}
</style>
