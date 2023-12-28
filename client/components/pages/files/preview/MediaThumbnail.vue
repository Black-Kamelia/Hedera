<script setup lang="ts">
const { data } = defineProps<{
  data: FileRepresentationDTO
}>()

const previewOpen = ref(false)

const { thumbnail, loading, error } = useThumbnail(data.code, data.mimeType)

const type = computed(() => mimeTypeToMediaType(data.mimeType))
</script>

<template>
  <div class="relative w-6rem h-4rem border-rounded-2 overflow-hidden">
    <ImagePreview v-if="type === 'image'" v-model:open="previewOpen" :file="data" />
    <VideoPreview v-else-if="type === 'video'" v-model:open="previewOpen" :file="data" />
    <AudioPreview v-else-if="type === 'audio'" v-model:open="previewOpen" :file="data" />
    <MediaPreview v-else v-model:open="previewOpen" :file="data" />

    <Thumbnail
      :src="thumbnail"
      :alt="data.name"
      :type="data.mimeType"
      :loading="loading"
      :error="error"
      @open-preview="previewOpen = true"
    />
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
