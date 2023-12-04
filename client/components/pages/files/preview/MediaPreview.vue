<script setup lang="ts">
interface Control {
  icon: string
  command: () => any
}

const { file, controls = [] } = defineProps<{
  file: FileRepresentationDTO
  controls?: Control[]
}>()

const emit = defineEmits<{
  (event: 'close'): void
}>()

const previewOpen = defineModel<boolean>('open', { default: false })
const { t } = useI18n()

function download() {
  $fetchAPI<Blob>(`/files/${file.code}`)
    .then(response => downloadBlob(response, file.name))
}

function open() {
  window.open(`/m/${file.code}`)
}

function onClose(closeCallback: () => void) {
  closeCallback()
  emit('close')
}
</script>

<template>
  <PDialog
    v-model:visible="previewOpen"
    modal
    unstyled
    dismissable-mask
    :pt="{
      mask: { class: 'display-none' },
      transition: () => ({
        enterFromClass: 'preview-enter-from',
        enterActiveClass: 'preview-enter-active',
        leaveActiveClass: 'preview-leave-active',
        leaveToClass: 'preview-leave-to',
      }),
    }"
  >
    <template #container="{ closeCallback }">
      <div class="mask" @click="onClose(closeCallback)" />
      <div class="controls blur-pill">
        <PButton
          icon="i-tabler-x"
          size="small"
          text
          rounded
          @click="onClose(closeCallback)"
        />
        <PButton
          icon="i-tabler-external-link"
          size="small"
          text
          rounded
          @click="open()"
        />
        <PButton
          v-for="control in controls"
          :key="control"
          :icon="control.icon"
          size="small"
          text
          rounded
          @click="control.command()"
        />
      </div>
      <div class="pointer-events-none w-screen h-screen">
        <div class="preview relative flex flex-row items-center justify-center p-25 h-full w-full max-h-100vh">
          <slot>
            <div class="text-white pointer-events-auto flex flex-col items-center justify-center gap-5">
              <i class="text-5xl" :class="mimeTypeToIcon(file.mimeType)" />
              <h1 class="text-2xl font-semibold">
                {{ t('pages.files.preview.no_preview') }}
              </h1>
              <PButton
                icon="i-tabler-download"
                :label="t('pages.files.preview.download')"
                rounded
                @click="download()"
              />
            </div>
          </slot>
        </div>
      </div>

      <div class="title blur-pill">
        {{ file.name }}
      </div>
    </template>
  </PDialog>
</template>

<style scoped lang="scss">
* {
  font-family: var(--font-family);
}

.mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(2px);
}

.blur-pill {
  background-color: rgba(0, 0, 0, 0.25);
  backdrop-filter: blur(7px);
  border-radius: 9999px;
  color: white;
}

.controls {
  position: absolute;
  right: 1.75rem;
  top: .75rem;
  display: flex;
  flex-direction: row-reverse;
  gap: .25em;
  padding: .25em;
  z-index: 5000;

  .p-button {
    color: var(--gray-50);
  }
}

.title {
  position: absolute;
  bottom: 2.5em;
  left: 50%;
  padding: .75em 1.5em;
  transform: translateX(-50%);
  z-index: 5000;
}
</style>

<style lang="scss">
.preview-enter-from,
.preview-leave-to {
  .mask {
    opacity: 0;
  }

  .preview {
    opacity: 0;
    transform: scale(.6);
  }

  .controls {
    transform: translateY(-200%);
  }

  .title {
    transform: translateY(300%) translateX(-50%);
  }
}

.preview-enter-active {
  transition: all .4s;

  .mask,
  .preview {
    transition: all .4s cubic-bezier(0.16, 1, 0.3, 1);
  }

  .title,
  .controls {
    transition: transform 0.35s cubic-bezier(0.25, 1, 0.5, 1);
  }
}

.preview-leave-active {
  transition: all .4s;

  .mask,
  .preview {
    transition: all .15s ease;
  }

  .title,
  .controls {
    transition: transform 0.4s cubic-bezier(0.5, 1, 0.89, 1);
  }
}
</style>
