<script lang="ts" setup>
const { selecting } = defineProps<{
  selecting: boolean
}>()

const emit = defineEmits<{
  (event: 'download'): void
  (event: 'changeVisibility'): void
  (event: 'unselect'): void
  (event: 'delete'): void
}>()

const { t } = useI18n()
</script>

<template>
  <div class="actions left-0 fixed bottom-8 flex flex-row flex-center w-full gap-2">
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.download'), class: 'translate-y--1' }"
          class="shadow-lg" icon="i-tabler-download" rounded @click="emit('download')"
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.change_visibility'), class: 'translate-y--1' }"
          class="shadow-lg" icon="i-tabler-eye" rounded @click="emit('changeVisibility')"
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.unselect'), class: 'translate-y--1' }"
          class="shadow-lg" icon="i-tabler-x" rounded @click="emit('unselect')"
        />
      </div>
    </Transition>
    <Transition>
      <div v-show="selecting">
        <PButton
          v-tooltip.top="{ value: t('pages.files.context_menu.delete'), class: 'translate-y--1' }"
          class="shadow-lg" icon="i-tabler-trash" severity="danger" rounded @click="emit('delete')"
        />
      </div>
    </Transition>
  </div>
</template>

<style scoped lang="scss">
@for $i from 1 through 5 {
  .actions .v-enter-active:nth-child(#{$i}n) {
    transition-delay: #{($i - 1) * 0.03}s;
  }
}

.actions {
  pointer-events: none;

  .p-button {
    pointer-events: auto;
  }
}

.v-enter-active {
  transition: all 0.35s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.v-leave-active {
  transition: all 0.35s cubic-bezier(0.5, 0, 0.75, 0);
}

.v-enter-from,
.v-leave-to {
  transform: translateY(150%);
  opacity: 0;
}
</style>
