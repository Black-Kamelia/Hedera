<script setup lang="ts">
import { useField } from 'vee-validate'
import type { CheckboxProps } from 'primevue/checkbox'
import PCheckbox from 'primevue/checkbox'

export interface FormCheckboxProps extends OnlyProps<CheckboxProps> {
  id: string
  name: string
  label: string
}

const { name, label } = defineProps<FormCheckboxProps>()

const { value } = useField<boolean>(name, _ => true, {
  validateOnValueUpdate: false,
})

const el = ref<Nullable<CompElement<InstanceType<typeof PCheckbox>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div class="flex flex-row items-center">
    <PCheckbox ref="el" v-bind="$attrs" v-model="value" :input-id="id" :name="name" class="mr-2" />
    <label :for="id">{{ label }}</label>
  </div>
</template>
