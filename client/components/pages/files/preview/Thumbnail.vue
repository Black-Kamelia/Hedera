<script setup lang="ts">
import { Vue3BlurhashCanvas } from 'vue3-blurhash'

interface ThumnailProps {
  src: string | null
  blurhash?: string | null
  alt: string
  type: string
  loading?: boolean
  error?: boolean | null
  previewable?: boolean
  width?: string
  height?: string
  borderRadius?: string
}

const {
  src,
  blurhash = null,
  alt,
  type,
  loading = false,
  error = false,
  previewable = true,
  width = '6rem',
  height = '4rem',
  borderRadius = '0.5rem',
} = defineProps<ThumnailProps>()

const emit = defineEmits<{
  (event: 'openPreview'): void
}>()

const el = ref<HTMLElement>()
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
      <div v-if="!imgError && (loading || src)" class="relative h-full w-full">
        <PSkeleton v-if="loading && !blurhash" width="6rem" height="4rem" />
        <img
          v-else-if="!loading && src"
          class="object-cover"
          :src="src"
          :alt="alt"
          :style="{ width, height }"
          @error="imgError = true"
        >
        <Transition v-if="blurhash" name="fade">
          <Vue3BlurhashCanvas
            v-show="loading"
            class="absolute"
            :hash="blurhash"
            :alt="alt"
            :style="{ width, height }"
          />
        </Transition>
      </div>
      <i v-else :class="icon" />
    </div>

    <Transition v-if="previewable">
      <a
        v-show="hovered"
        class="absolute flex flex-center bg-[var(--primary-color-transparent)] backdrop-blur-md w-full h-full text-white cursor-pointer"
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

  img {
    position: absolute;
  }
}

.error {
  &::before {
    background-color: var(--red-500);
  }

  & > i {
    color: var(--red-500);
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

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
