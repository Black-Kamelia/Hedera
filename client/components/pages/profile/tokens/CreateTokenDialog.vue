<script lang="ts" setup>
import { object, string } from 'yup'
import { useForm } from 'vee-validate'

const emit = defineEmits<{
  (event: 'completed', payload: PersonalTokenDTO): void
}>()

const { t } = useI18n()

const visible = defineModel<boolean>('visible', { default: true })
const pending = ref(false)
const createToken = useCreateToken()

const schema = object({
  name: string()
    .required(t('pages.profile.tokens.create_dialog.errors.missing_name')),
})
const { handleSubmit } = useForm({
  validationSchema: schema,
})

const submit = handleSubmit(async (values) => {
  pending.value = true
  createToken(values.name).then((response) => {
    if (response) {
      emit('completed', response.payload)
      pending.value = false
      visible.value = false
    }
  })
})
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="max-w-75% xl:max-w-50%"
    :header="t('pages.profile.tokens.create_dialog.title')"
    :draggable="false"
  >
    <div class="flex flex-col gap-3">
      <p class="text-[--text-color-secondary]">
        {{ t('pages.profile.tokens.create_dialog.summary') }}
      </p>

      <FormInputText
        id="token_name"
        name="name"
        :placeholder="t('pages.profile.tokens.create_dialog.fields.name')"
        class="w-full"
        autofocus
        @keydown.enter="submit()"
      />
    </div>

    <template #footer>
      <PButton
        :label="t('pages.profile.tokens.create_dialog.cancel')"
        text
        :disabled="pending"
        @click="visible = false"
      />
      <PButton
        :label="t('pages.profile.tokens.create_dialog.submit')"
        :loading="pending"
        type="submit"
        @click="submit()"
      />
    </template>
  </PDialog>
</template>
