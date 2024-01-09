<script lang="ts" setup>
import { decode } from 'blurhash'

interface BlurhashProps {
  hash: string
  width?: number
  height?: number
  punch?: number
}

const {
  hash,
  width = 128,
  height = 128,
  punch = 1,
} = defineProps<BlurhashProps>()

const canvas = ref<HTMLCanvasElement>()

function draw() {
  if (!canvas.value) return
  const pixels = decode(hash, width, height, punch)
  const ctx = canvas.value.getContext('2d')
  if (!ctx) return
  const imageData = ctx.createImageData(width, height)
  imageData.data.set(pixels)
  ctx.putImageData(imageData, 0, 0)
}

onMounted(() => {
  draw()
})

onUpdated(() => {
  draw()
})
</script>

<template>
  <canvas ref="canvas" :width="width" :height="height" />
</template>
