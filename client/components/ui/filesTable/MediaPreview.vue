<script setup lang="ts">
const { data } = definePropsRefs<{
  data: FileRepresentationDTO
}>()

const el = ref()
const hovered = useElementHover(el)

const { thumbnail, isLoading, isError } = useThumbnail(data.value.code)
</script>

<template>
  <div
    ref="el"
    class="relative w-6rem h-4rem border-rounded-2 overflow-hidden"
  >
    <div
      class="absolute flex flex-center w-full h-full" :class="{
        preview: !data.mimeType.startsWith('image/') || (!isLoading && !thumbnail),
        error: !isLoading && isError,
      }"
    >
      <div v-if="data.mimeType.startsWith('image/') && (isLoading || thumbnail)" class="h-full w-full">
        <PSkeleton v-if="isLoading" width="6rem" height="4rem" />
        <img
          v-if="!isLoading && thumbnail"
          class="w-6rem h-4rem object-cover"
          :src="thumbnail"
          :alt="data.name"
        >
      </div>
      <i v-else-if="data.mimeType.startsWith('image/') && !isLoading && !thumbnail && !isError" class="i-tabler-photo-x" />
      <i v-else-if="data.mimeType.startsWith('image/') && !isLoading && isError" class="i-tabler-photo-exclamation" />
      <i v-else-if="data.mimeType.startsWith('audio/')" class="i-tabler-music" />
      <i v-else-if="data.mimeType.startsWith('video/')" class="i-tabler-video" />
      <i v-else-if="data.mimeType.startsWith('text/')" class="i-tabler-file-text" />
      <i v-else-if="data.mimeType === 'application/zip'" class="i-tabler-file-zip" />
      <i v-else-if="data.mimeType === 'application/pdf'" class="i-tabler-file-text" />
      <i v-else-if="data.mimeType === 'application/unknown'" class="i-tabler-file-unknown" />
      <i v-else class="i-tabler-file" />
    </div>
    <Transition>
      <a
        v-show="hovered"
        href="#"
        class="absolute flex flex-center bg-[var(--primary-color-transparent)] backdrop-blur-sm border-rounded-2 w-full h-full text-white cursor-pointer"
      >
        <i class="text-base i-tabler-eye" />
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

.error::after {
  content: '';
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: var(--red-500);
  opacity: 12.5%;
}

.error > i {
  display: block;
  opacity: 100%;
  color: var(--red-500);
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
