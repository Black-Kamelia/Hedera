<script setup lang="ts">
import { useField } from 'vee-validate'
import type { CheckboxProps } from 'primevue/checkbox'
import PCheckbox from 'primevue/checkbox'

export interface FormCheckboxProps extends CheckboxProps {
  name: string
  label: string
}

const { name, label } = definePropsRefs<FormCheckboxProps>()

const { value } = useField<boolean>(name)

const el = ref<Nullable<CompElement<InstanceType<typeof PCheckbox>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div class="flex flex-row items-center">
    <PCheckbox :id="name" ref="el" v-bind="$attrs" v-model="value" class="mr-2" />
    <label :for="name" class="text-gray">{{ label }}</label>
  </div>
</template>
