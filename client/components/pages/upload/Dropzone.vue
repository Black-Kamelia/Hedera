<script setup lang="ts">
const { t } = useI18n()

const files = defineModel<File[]>('files', { default: [] })
const empty = computed(() => files.value.length === 0)
const dropzone = ref<HTMLElement>()

const { isOverDropZone } = useDropZone(dropzone, { onDrop })
const { onChange, open } = useFileDialog({ multiple: true })

onChange((uploadedFiles) => {
  if (!uploadedFiles) return
  for (let i = 0; i < uploadedFiles.length; i++) {
    files.value.push(uploadedFiles[i])
  }
})

function onDrop(droppedFiles: File[] | null) {
  if (droppedFiles) files.value.push(...droppedFiles)
}

function onRemove(index: number) {
  files.value.splice(index, 1)
}
</script>

<template>
  <div ref="dropzone" class="dropzone" :class="{ 'dropzone-over': isOverDropZone }">
    <div v-if="empty" class="area">
      <img class="w-10em" src="/assets/img/new_file.png" alt="">
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

    <div v-else class="files">
      <FileTile
        v-for="(file, index) of files"
        :key="file.name + file.type + file.size"
        :file="file"
        status="completed"
        @remove="onRemove(index)"
      />
    </div>

    <Transition name="grow">
      <div v-if="isOverDropZone" class="dropzone-release">
        <h1 class="text-2xl">
          {{ t('pages.upload.dropzone.drop_here') }}
        </h1>
        <p>
          {{ t('pages.upload.dropzone.drop_here_subtitle') }}
        </p>
      </div>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
.dropzone {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
  overflow: hidden;

  & .area {
    border: 2px dashed var(--surface-border);
    border-radius: 0.5rem;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    text-align: center;
    height: 100%;
    width: 100%;
    transition: all .2s cubic-bezier(0.25, 1, 0.5, 1);
  }

  & .files {
    border: 2px dashed var(--surface-border);
    border-radius: 0.5rem;
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

  & .area,
  & .files{
    border-radius: 0.5rem;
    border: 2px solid var(--primary-500);
  }
}

.dropzone-over::after {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: var(--primary-300);
  opacity: 25%;
  border-radius: .5em;
  z-index: 200;
}

.dropzone-release {
  position: absolute;
  background: var(--primary-500);
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
    padding: 3em;
  }
  100% {
    padding: 6em;
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
</style>
