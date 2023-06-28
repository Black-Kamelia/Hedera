<script lang="ts" setup>
const { name: originalName } = defineProps<{
  name: string
}>()
const emit = defineEmits<{
  (event: 'completed', name: string): void
}>()
const visible = defineModel<boolean>('visible')
const { t } = useI18n()

const name = ref(originalName)
watch(visible, (val) => {
  if (val)
    name.value = originalName
}, { immediate: true })

function submit() {
  visible.value = false
  emit('completed', name.value)
}
</script>

<template>
  <PDialog v-model:visible="visible" class="min-w-30em" modal :header="t('pages.files.rename.title')" :draggable="false">
    <PInputText v-model="name" class="w-full mt-0.5" autofocus @keydown.enter="submit()" />

    <template #footer>
      <PButton :label="t('pages.files.rename.cancel')" text @click="visible = false" />
      <PButton :label="t('pages.files.rename.submit')" icon="i-tabler-check" @click="submit()" />
    </template>
  </PDialog>
</template>
