<script setup lang="ts">
const { t } = useI18n()

const files = defineModel<FileUpload[]>('files', { default: [] })
const empty = computed(() => files.value.length === 0)
const dropzone = ref<HTMLElement>()

const { isOverDropZone } = useDropZone(dropzone, { onDrop })
const { onChange, open } = useFileDialog({ multiple: true })

onChange((uploadedFiles) => {
  if (!uploadedFiles) return
  for (let i = 0; i < uploadedFiles.length; i++) {
    files.value.push({ file: uploadedFiles[i], status: 'pending' })
  }
})

function onDrop(droppedFiles: File[] | null) {
  if (droppedFiles) {
    const newFiles = droppedFiles.map(file => ({ file, status: 'pending' } satisfies FileUpload))
    files.value.push(...newFiles)
  }
}

function onRemove(index: number) {
  files.value.splice(index, 1)
}

function onBeforeLeave(el: Element) {
  const htmlEl = el as HTMLElement
  const { marginLeft, marginTop, width, height } = window.getComputedStyle(el)
  htmlEl.style.left = `${htmlEl.offsetLeft - Number.parseFloat(marginLeft)}px`
  htmlEl.style.top = `${htmlEl.offsetTop - Number.parseFloat(marginTop)}px`
  htmlEl.style.width = width
  htmlEl.style.height = height
  htmlEl.style.zIndex = '1'
}

const filesUploadState = useEmptyState('files_upload')
</script>

<template>
  <div ref="dropzone" class="dropzone" :class="{ 'dropzone-over': isOverDropZone }">
    <Transition name="fade">
      <div v-show="empty" class="placeholder select-none">
        <img class="w-10em pointer-events-none" :src="filesUploadState" alt="">
        <h1 class="text-2xl">
          {{ t('pages.upload.dropzone.title') }}
        </h1>
        <p class="pb-10">
          {{ t('pages.upload.dropzone.description') }}
        </p>
        <PButton
          rounded
          :label="t('pages.upload.select_files')"
          @click="open()"
        />
      </div>
    </Transition>

    <TransitionGroup name="list" class="files" tag="div" @before-leave="onBeforeLeave">
      <FileTile
        v-for="({ file, status, statusDetail }, index) of files"
        :key="file.name + file.type + file.size"
        :file="file"
        :status="status"
        :reason="statusDetail"
        @remove="onRemove(index)"
      />
    </TransitionGroup>

    <Transition name="grow">
      <div v-if="isOverDropZone" class="dropzone-release">
        <h1 class="text-2xl">
          {{ t('pages.upload.dropzone.drop_here') }}
        </h1>
      </div>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
.list {
  &-move {
    transition: all .3s cubic-bezier(0.65, 0, 0.35, 1);
  }
  &-enter-active {
    transition: all .2s cubic-bezier(0.33, 1, 0.68, 1);
  }
  &-leave-active {
    transition: all .2s cubic-bezier(0.32, 0, 0.67, 0);
  }

  &-leave-active {
    position: absolute;
  }

  &-enter-from,
  &-leave-to {
    opacity: 0;
    transform: scale(0.5);
  }
}

.dropzone {
  position: relative;
  border: 2px dashed var(--surface-border);
  border-radius: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  overflow: hidden;

  & .placeholder {
    position: absolute;
    top: 0;
    left: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    height: 100%;
    width: 100%;
    transition: all .2s cubic-bezier(0.25, 1, 0.5, 1);
    z-index: 10;
  }

  & .files {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    align-content: flex-start;
    padding: 1em;
    gap: 1em;
    height: 100%;
    width: 100%;
    overflow-y: auto;
  }
}

.dropzone-over {
  position: relative;
  border-radius: 0.5rem;
  border: 2px solid var(--primary-500);
}

.dropzone-over::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: var(--primary-100);
  opacity: 65%;
  z-index: 200;
}

html.dark .dropzone-over::after {
  background-color: var(--primary-900);
  opacity: 50%;
}

.dropzone-release {
  position: absolute;
  background: var(--primary-500);
  outline: 0 solid var(--primary-color-transparent);
  color: #fff;
  border-radius: 50%;
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  animation: breathing 2s cubic-bezier(0.76, 0, 0.24, 1) infinite alternate;
  z-index: 201;
}

@keyframes breathing {
  0% {
    padding: 2em;
    outline-width: 3em;
  }
  100% {
    padding: 4em;
    outline-width: 2em;
  }
}

.grow-enter-active {
  transition: all 0.2s cubic-bezier(0.25, 1, 0.5, 1);
}
.grow-leave-active {
  transition: all 0.2s cubic-bezier(0.5, 0, 0.75, 0);
}

.grow-enter-from,
.grow-leave-to {
  opacity: 0;
  transform: scale(.5);
}

.fade-enter-active {
  transition: all 0.2s cubic-bezier(0.25, 1, 0.5, 1);
}
.fade-leave-active {
  transition: all 0.2s cubic-bezier(0.5, 0, 0.75, 0);
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
