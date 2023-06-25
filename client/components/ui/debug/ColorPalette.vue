<script lang="ts" setup>
export interface ColorPaletteProps {
  color: string
}

const { color } = definePropsRefs<ColorPaletteProps>()
const SHADES = [50, 100, 200, 300, 400, 500, 600, 700, 800, 900]
</script>

<template>
  <div class="flex flex-col gap-1">
    <div v-for="shade in SHADES" :key="shade" class="p-1 rounded" :style="`background-color: var(--${color}-${shade})`">
      <span class="bg-black/50 px-1 rounded text-white">{{ color }}-{{ shade }}</span>
    </div>
  </div>
</template>
