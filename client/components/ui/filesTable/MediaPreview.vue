<script setup lang="ts">
const { data } = definePropsRefs<{
  data: any
}>()

const el = ref()
const hovered = useElementHover(el)
</script>

<template>
  <div
    ref="el"
    class="relative w-6rem h-4rem border-rounded-2 overflow-hidden"
  >
    <div class="absolute flex flex-center w-full h-full" :class="{ preview: !data.type.startsWith('image/') }">
      <PImage
        v-if="data.type.startsWith('image/')"
        class="w-6rem h-4rem object-cover"
        :src="`https://picsum.photos/1600/900?random=${data.id}`"
        :alt="data.name"
        preview
      />
      <i v-else-if="data.type === 'audio/mpeg'" class="i-tabler-file-music" />
      <i v-else class="i-tabler-file" />
    </div>
    <Transition>
      <a
        v-show="false"
        href="#"
        class="absolute flex flex-center bg-black/12.5 backdrop-blur-sm border-rounded-2 w-full h-full text-white"
      >
        <i class="text-base i-tabler-external-link" />
      </a>
    </Transition>
  </div>
</template>

<style scoped>
.preview::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: var(--primary-500);
  opacity: 12.5%;
}

.preview > i {
  display: block;
  opacity: 100%;
  color: var(--primary-500);
  font-size: 1.5rem;
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
