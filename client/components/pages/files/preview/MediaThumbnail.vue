<script setup lang="ts">
const { data } = defineProps<{
  data: FileRepresentationDTO
}>()

const el = ref()
const hovered = useElementHover(el)
const previewOpen = ref(false)

const { thumbnail, isLoading, isError } = useThumbnail(data.code, data.mimeType)

const type = computed(() => mimeTypeToMediaType(data.mimeType))
const icon = computed(() => {
  switch (type.value) {
    case 'image':
      if (isError.value) return 'i-tabler-photo-exclamation'
      if (!isLoading.value && !thumbnail.value) return 'i-tabler-photo-x'
      return 'i-tabler-photo'
    case 'audio':
      return 'i-tabler-music'
    case 'video':
      return 'i-tabler-video'
    case 'text':
      return 'i-tabler-file-text'
    case 'zip':
      return 'i-tabler-file-zip'
    case 'document':
      return 'i-tabler-file-text'
    case 'unknown':
      return 'i-tabler-file-unknown'
    default:
      return 'i-tabler-file'
  }
})
</script>

<template>
  <div
    ref="el"
    class="relative w-6rem h-4rem border-rounded-2 overflow-hidden"
  >
    <ImagePreview v-if="type === 'image'" v-model:open="previewOpen" :file="data" />
    <VideoPreview v-else-if="type === 'video'" v-model:open="previewOpen" :file="data" />
    <AudioPreview v-else-if="type === 'audio'" v-model:open="previewOpen" :file="data" />
    <MediaPreview v-else v-model:open="previewOpen" :file="data" />

    <div
      class="absolute flex flex-center w-full h-full preview"
      :class="{ error: !isLoading && isError }"
    >
      <div v-if="isLoading || thumbnail" class="h-full w-full">
        <PSkeleton v-if="isLoading" width="6rem" height="4rem" />
        <img
          v-if="!isLoading && thumbnail"
          class="w-6rem h-4rem object-cover"
          :src="thumbnail"
          :alt="data.name"
          @error="thumbnail = null"
        >
      </div>
      <i v-else :class="icon" />
    </div>

    <Transition>
      <a
        v-show="hovered || previewOpen"
        class="absolute flex flex-center bg-[var(--primary-color-transparent)] backdrop-blur-md border-rounded-2 w-full h-full text-white cursor-pointer"
        @click="previewOpen = true"
      >
        <i class="text-base i-tabler-eye" />
      </a>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
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
