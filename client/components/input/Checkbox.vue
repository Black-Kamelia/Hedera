<script setup lang="ts">
import { useField } from 'vee-validate'
import _CheckboxProps from 'primevue/checkbox'

interface CheckboxProps extends _CheckboxProps {
  name: string
  label: string
}

const props = defineProps<CheckboxProps>()

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
