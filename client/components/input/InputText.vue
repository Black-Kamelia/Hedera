<script setup lang="ts">
import { useField } from 'vee-validate'
import _InputTextProps from 'primevue/inputtext'

interface InputTextProps extends _InputTextProps {
  name: string
}

const props = defineProps<InputTextProps>()

const { errorMessage, value } = useField(props.name)
</script>

<template>
  <PInputText v-bind="$attrs" v-model="value" :class="{ 'p-invalid': errorMessage }" />
</template>
