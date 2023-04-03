<script setup>
import { useField } from 'vee-validate'
import CheckboxProps from 'primevue/checkbox'

const props = defineProps({
  ...CheckboxProps,
  label: {
    type: String,
    required: true,
  },
})

const { value } = useField(props.name)
</script>

<template>
  <div class="flex flex-row items-center">
    <PCheckbox :id="props.name" v-bind="$attrs" v-model="value" class="mr-2" />
    <label :for="props.name" class="text-gray">{{ props.label }}</label>
  </div>
</template>

<style scoped>

</style>
