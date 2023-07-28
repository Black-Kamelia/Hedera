<script lang="ts" setup>
import type { DynamicDialogInstance } from 'primevue/dynamicdialogoptions'
import type { ComputedRef } from 'vue'
import PInputText from 'primevue/inputtext'

const { t } = useI18n()

const dialog = inject<ComputedRef<DynamicDialogInstance>>('dialogRef')
const input = ref<Nullable<CompElement<InstanceType<typeof PInputText>>>>(null)

const name = ref('')

function submit() {
  dialog?.value.close({ name: name.value })
}
</script>

<template>
  <div class="flex flex-col gap-3">
    <p class="text-[--text-color-secondary]">
      {{ t('pages.profile.tokens.create_dialog.summary') }}
    </p>

    <PInputText
      ref="input"
      v-model="name"
      :placeholder="t('pages.profile.tokens.create_dialog.fields.name')"
      class="w-full mt-0.5"
      autofocus
      @keydown.enter="submit()"
    />
  </div>
</template>
