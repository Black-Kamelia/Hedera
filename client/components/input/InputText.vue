<script setup lang="ts">
import { useField } from 'vee-validate'
import type _InputTextProps from 'primevue/inputtext'
import type { Nullable } from 'primevue/ts-helpers'

interface InputTextProps extends _InputTextProps {
  name: string
}

const props = defineProps<InputTextProps>()

const { errorMessage, value } = useField<Nullable<string>>(props.name)
</script>

<template>
  <PInputText v-bind="$attrs" v-model="value" :class="{ 'p-invalid': errorMessage }" />
</template>
