<script lang="ts" setup>
import { object, string } from 'yup'
import getSharexConfiguration from '~/utils/uploaderConfigs/getSharexConfiguration'
import getUpicConfiguration from '~/utils/uploaderConfigs/getUpicConfiguration'

const emit = defineEmits<{
  (event: 'completed', payload: PersonalTokenDTO): void
}>()

const { t } = useI18n()
const createToken = useCreateToken()
const { copyToken, isSupported } = useCopyToken()

const visible = defineModel<boolean>('visible', { default: false })
const pending = ref(false)
const newToken = ref<Nullable<PersonalTokenDTO>>(null)
const token = computed(() => newToken.value?.token ?? '')

const schema = object({
  name: string()
    .required(t('pages.profile.tokens.create_dialog.errors.missing_name')),
})
const { handleSubmit, resetForm } = useForm({
  validationSchema: schema,
})

const submit = handleSubmit(async (values) => {
  pending.value = true
  createToken(values.name).then((response) => {
    if (response) {
      newToken.value = response.payload
      emit('completed', response.payload!)
    }
  }).finally(() => pending.value = false)
})

function onHide() {
  newToken.value = null
  resetForm()
}
</script>

<template>
  <PDialog
    v-model:visible="visible"
    modal
    class="mx-4 sm:w-45em md:mx-0 md:max-w-75%"
    :header="t('pages.profile.tokens.create_dialog.title')"
    :draggable="false"
    :dismissable-mask="newToken !== null"
    :pt="{ content: { class: 'important-overflow-hidden' } }"
    @hide="onHide"
  >
    <div class="relative">
      <SlideTransitionContainer>
        <div v-if="!newToken">
          <div class="flex flex-col gap-3">
            <p class="text-[--text-color-secondary]">
              {{ t('pages.profile.tokens.create_dialog.summary') }}
            </p>

            <FormInputText
              id="token_name"
              name="name"
              :label="t('pages.profile.tokens.create_dialog.fields.name')"
              :placeholder="t('pages.profile.tokens.create_dialog.fields.name_placeholder', { name: getRandomDeveloper().firstName })"
              class="w-full"
              autofocus
              @keydown.enter="submit"
            />
          </div>

          <div class="flex flex-row justify-end gap-2 pt-9 items-center">
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
              icon="i-tabler-check"
              @click="submit"
            />
          </div>
        </div>

        <div v-else class="flex flex-col gap-3">
          <p class="text-[--text-color-secondary]">
            {{ t('pages.profile.tokens.generated_dialog.summary') }}
          </p>
          <div class="flex flex-row gap-3 mb-3">
            <PInputText class="flex-grow" readonly :value="token" />
            <PButton
              v-if="isSupported" :label="t('pages.profile.tokens.copy_token')" icon="i-tabler-copy"
              @click="copyToken(token)"
            />
          </div>
          <p class="text-[--text-color-secondary]">
            {{ t('pages.profile.tokens.generated_dialog.download') }}
          </p>
          <div class="flex flex-row justify-between">
            <div class="flex flex-row gap-2 items-center">
              <PButton
                size="small"
                severity="secondary"
                label="ShareX"
                @click="getSharexConfiguration(newToken!.name, token)"
              />
              <PButton
                size="small"
                severity="secondary"
                label="uPic"
                @click="getUpicConfiguration(newToken!.name, token)"
              />
            </div>
            <div class="flex flex-row-reverse gap-3 items-center">
              <PButton size="small" text :label="t('pages.profile.tokens.create_dialog.documentation')" icon="i-tabler-help-circle-filled" />
              <PButton size="small" text :label="t('pages.profile.tokens.create_dialog.propose_app')" icon="i-tabler-message-report" />
            </div>
          </div>
        </div>
      </SlideTransitionContainer>
    </div>
  </PDialog>
</template>
