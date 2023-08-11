<script setup lang="ts">
import { useField } from 'vee-validate'
import type PInputText from 'primevue/inputtext'
import type { DropdownProps } from 'primevue/dropdown'

export interface FormDropdownProps extends OnlyProps<DropdownProps> {
  id: string
  name: string
  label: string
  options: any[]
  startIcon?: string
  endIcon?: string
  transformValue?: (value: string) => string
}

const { id, name, label, startIcon, endIcon, transformValue = value => value } = defineProps<FormDropdownProps>()
const { errorMessage, value } = useField<Nullable<string>>(name)

function onInput(payload: Event) {
  const target = payload.target as HTMLInputElement | null
  value.value = transformValue(target?.value ?? '')
}

const el = ref<Nullable<CompElement<InstanceType<typeof PInputText>>>>()
defineExpose({
  $el: computed(() => el.value?.$el),
})
</script>

<template>
  <div>
    <label v-if="label" :for="id" class="block font-900 font-medium mb-2">{{ label }}</label>
    <span class="w-full" :class="{ 'p-input-icon-left': startIcon, 'p-input-icon-right': endIcon }">
      <i v-if="startIcon" :class="startIcon" />
      <PDropdown
        :id="id"
        v-bind="$attrs"
        ref="el"
        v-model="value"
        :class="{ 'p-invalid': errorMessage }"
        :options="options"
        @input="onInput"
      />
      <i v-if="endIcon" :class="endIcon" />
    </span>
    <Transition>
      <small v-if="errorMessage" :id="`${id}-error`" class="mt-1 block p-error">{{ errorMessage }}</small>
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
