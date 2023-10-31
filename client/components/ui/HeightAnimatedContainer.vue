<script lang="ts" setup>
const { preventTransition = false } = defineProps<{
  preventTransition?: boolean
}>()

const child = ref()
const { height } = useElementSize(child)
</script>

<template>
  <div
    class="relative w-full"
    :class="{ 'h-transition': !preventTransition }"
    :style="{ height: `${height}px` }"
  >
    <div ref="child">
      <slot />
    </div>
  </div>
</template>

<style scoped>
.h-transition {
  transition: height 0.4s cubic-bezier(0.87, 0, 0.13, 1);
}
</style>
