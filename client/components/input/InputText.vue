<script setup lang="ts">
import { useField } from 'vee-validate'
import type _InputTextProps from 'primevue/inputtext'
import PInputText from 'primevue/inputtext'

export interface InputTextProps extends _InputTextProps {
  id: string
  name: string
  label: string
  startIcon?: string
  endIcon?: string
}

const { id, name, label, startIcon, endIcon } = definePropsRefs<InputTextProps>()

const { errorMessage, value } = useField<Nullable<string>>(name)

const el = ref<Nullable<CompElement<InstanceType<typeof PInputText>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <label :for="id" class="block font-900 font-medium mb-2">{{ label }}</label>
  <div class="mb-3">
    <span class="w-full mb-1" :class="{ 'p-input-icon-left': startIcon, 'p-input-icon-right': endIcon }">
      <i v-if="startIcon" :class="startIcon" />
      <PInputText :id="id" v-bind="$attrs" ref="el" v-model="value" :class="{ 'p-invalid': errorMessage }" />
      <i v-if="endIcon" :class="endIcon" />
    </span>
    <Transition>
      <small v-if="errorMessage" id="text-error" class="block p-error">{{ errorMessage }}</small>
    </Transition>
  </div>
</template>

<style scoped>
small {
  height: 1rem;
}

.v-enter-active,
.v-leave-active {
  transition: all 0.2s ease-in-out;
}

.v-enter-from,
.v-leave-to {
  opacity: 0;
  height: 0;
  transform: translateY(-1rem);
}

.v-enter-to,
.v-leave-from {
  opacity: 1;
  height: 1rem;
  transform: translateY(0);
}
</style>
