<script setup lang="ts">
import { useField } from 'vee-validate'
import type _InputTextProps from 'primevue/inputtext'
import PInputText from 'primevue/inputtext'

export interface InputTextProps extends _InputTextProps {
  name: string
}

const { name } = definePropsRefs<InputTextProps>()

const { errorMessage, value } = useField<Nullable<string>>(name)

const el = ref<Nullable<CompElement<InstanceType<typeof PInputText>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <PInputText v-bind="$attrs" ref="el" v-model="value" :class="{ 'p-invalid': errorMessage }" />
</template>
