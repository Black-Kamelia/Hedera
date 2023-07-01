<script lang="ts" setup>
import type { DynamicDialogInstance } from 'primevue/dynamicdialogoptions'
import type { ComputedRef } from 'vue'
import PInputText from 'primevue/inputtext'

const dialog = inject<ComputedRef<DynamicDialogInstance>>('dialogRef')
const input = ref<Nullable<CompElement<InstanceType<typeof PInputText>>>>(null)

const store = useRenameFileDialog()
const { name } = storeToRefs(store)

onMounted(() => {
  name.value = dialog?.value.data?.name
  input.value?.$el.focus()
})

function submit() {
  dialog?.value.close({ newName: name.value })
}
</script>

<template>
  <PInputText ref="input" v-model="name" class="w-full mt-0.5" autofocus @keydown.enter="submit()" />
</template>
