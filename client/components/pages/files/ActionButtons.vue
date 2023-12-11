<script lang="ts" setup>
const selection = defineModel<FileRepresentationDTO[]>('selection', { default: () => [] })
const selecting = computed(() => selection.value.length > 0)

const { t } = useI18n()
</script>

<template>
  <div class="actions">
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.unselect'), class: 'translate-y--1' }"
          class="shadow-lg"
          icon="i-tabler-x"
          rounded
          @click="selection = []"
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.download'), class: 'translate-y--1' }"
          class="shadow-lg"
          icon="i-tabler-download"
          rounded
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.change_visibility'), class: 'translate-y--1' }"
          class="shadow-lg"
          icon="i-tabler-eye-edit"
          rounded
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.delete'), class: 'translate-y--1' }"
          class="shadow-lg"
          icon="i-tabler-trash"
          severity="danger"
          rounded
        />
      </div>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
@for $i from 1 through 4 {
  .actions .v-enter-active:nth-child(#{$i}n) {
    transition-delay: #{($i - 1) * 0.04}s;
  }
}

.actions {
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  padding: 2em .75em .75em .75em;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: .75em;
  z-index: 10;
  overflow:hidden;
}

.v-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.v-leave-active {
  transition: all 0.4s cubic-bezier(0.5, 0, 0.75, 0);
}

.v-enter-from,
.v-leave-to {
  transform: translateY(300%);
  opacity: 0;
}
</style>
