<script setup>
import { useField } from 'vee-validate'
import InputTextProps from 'primevue/inputtext'

const props = defineProps({
  ...InputTextProps,
})

const { errorMessage, value } = useField(props.name)
</script>

<template>
  <PInputText v-bind="$attrs" v-model="value" :class="{ 'p-invalid': errorMessage }" />
</template>
