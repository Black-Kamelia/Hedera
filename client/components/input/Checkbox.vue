<script setup lang="ts">
import { useField } from 'vee-validate'
import type _CheckboxProps from 'primevue/checkbox'
import PCheckbox from 'primevue/checkbox'

interface CheckboxProps extends _CheckboxProps {
  name: string
  label: string
}

const props = defineProps<CheckboxProps>()

const { value } = useField<boolean>(props.name)

const el = ref<Nullable<CompElement<InstanceType<typeof PCheckbox>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div class="flex flex-row items-center">
    <PCheckbox :id="props.name" ref="el" v-bind="$attrs" v-model="value" class="mr-2" />
    <label :for="props.name" class="text-gray">{{ props.label }}</label>
  </div>
</template>
