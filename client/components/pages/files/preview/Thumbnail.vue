<script setup lang="ts">
const {
  src,
  alt,
  type,
  loading = false,
  error = false,
  previewable = true,
  width = '6rem',
  height = '4rem',
  borderRadius = '0.5rem',
} = defineProps<{
  src: string | null
  alt: string
  type: string
  loading?: boolean
  error?: boolean
  previewable?: boolean
  width?: string
  height?: string
  borderRadius?: string
}>()

const emit = defineEmits<{
  (event: 'openPreview'): void
}>()

const el = ref()
const hovered = useElementHover(el)

const imgError = ref(false)

const icon = computed(() => {
  switch (mimeTypeToMediaType(type)) {
    case 'image':
      if (error) return 'i-tabler-photo-exclamation'
      if (!loading && !src) return 'i-tabler-photo-x'
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
    class="relative overflow-hidden"
    :style="{ width, height, borderRadius }"
  >
    <div
      class="absolute flex flex-center w-full h-full preview"
      :class="{ error: !loading && error }"
    >
      <div v-if="!imgError && (loading || src)" class="h-full w-full">
        <PSkeleton v-if="loading" width="6rem" height="4rem" />
        <img
          v-if="!loading && src"
          class="object-cover"
          :src="src"
          :alt="alt"
          :style="{ width, height }"
          @error="imgError = true"
        >
      </div>
      <i v-else :class="icon" />
    </div>

    <Transition v-if="previewable">
      <a
        v-show="hovered"
        class="absolute flex flex-center bg-[var(--primary-color-transparent)] backdrop-blur-md w-full h-full text-white cursor-pointer z-11"
        :style="{ borderRadius }"
        @click="emit('openPreview')"
      >
        <i class="text-base i-tabler-eye" />
      </a>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
.preview {
  &::before {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: var(--primary-500);
    opacity: 12.5%;
  }

  & > i {
    display: block;
    opacity: 100%;
    color: var(--primary-500);
    font-size: 1.5rem;
  }

  & img {
    position: absolute;
  }
}

.error {
  &::after {
    content: '';
    position: absolute;
    width: 100%;
    height: 100%;
    background-color: var(--red-500);
    opacity: 12.5%;
  }

  & > i {
    display: block;
    opacity: 100%;
    color: var(--red-500);
    font-size: 1.5rem;
  }
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
