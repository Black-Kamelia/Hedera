<script setup lang="ts">
const { data } = defineProps<{
  data: FileRepresentationDTO
}>()

const el = ref()
const hovered = useElementHover(el)
const previewOpen = ref(false)

const { thumbnail, isLoading, isError } = useThumbnail(data.code, data.mimeType)

const icon = computed(() => {
  if (data.mimeType.startsWith('image/')) {
    if (isError) return 'i-tabler-photo-exclamation'
    if (!isLoading && !thumbnail) return 'i-tabler-photo-x'

    return 'i-tabler-photo'
  }
  if (data.mimeType.startsWith('audio/')) return 'i-tabler-music'
  if (data.mimeType.startsWith('video/')) return 'i-tabler-video'
  if (data.mimeType.startsWith('text/')) return 'i-tabler-file-text'
  if (data.mimeType === 'application/zip') return 'i-tabler-file-zip'
  if (data.mimeType === 'application/pdf') return 'i-tabler-file-text'
  if (data.mimeType === 'application/unknown') return 'i-tabler-file-unknown'
  return 'i-tabler-file'
})
</script>

<template>
  <div
    ref="el"
    class="relative w-6rem h-4rem border-rounded-2 overflow-hidden"
    @click="previewOpen = true"
  >
    <PDialog v-model:visible="previewOpen" modal>
      <template #container>
        <p>MEDIA</p>
      </template>
    </PDialog>

    <div
      class="absolute flex flex-center w-full h-full" :class="{
        preview: !isLoading && !thumbnail,
        error: !isLoading && isError,
      }"
    >
      <div v-if="isLoading || thumbnail" class="h-full w-full">
        <PSkeleton v-if="isLoading" width="6rem" height="4rem" />
        <img
          v-if="!isLoading && thumbnail"
          class="w-6rem h-4rem object-cover"
          :src="thumbnail"
          :alt="data.name"
        >
      </div>
      <i v-else :class="icon" />
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
