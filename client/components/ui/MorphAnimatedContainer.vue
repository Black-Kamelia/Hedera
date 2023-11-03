<script lang="ts" setup>
const {
  preventTransition = false,
  animateHeight = true,
  animateWidth = true,
} = defineProps<{
  preventTransition?: boolean
  animateHeight?: boolean
  animateWidth?: boolean
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
  transition: width .5s cubic-bezier(0.65, 0, 0.35, 1), height .5s cubic-bezier(0.65, 0, 0.35, 1);
}
</style>
