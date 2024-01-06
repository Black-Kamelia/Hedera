<script setup lang="ts">
const { t } = useI18n()

type ResetPasswordState = 'REQUEST_TOKEN' | 'INPUT_TOKEN' | 'RESET_PASSWORD'

usePageName(() => t('pages.reset_password.title'))
definePageMeta({
  layout: 'auth',
  middleware: ['auth', 'card-transitions'],
})

const { currentRoute } = useRouter()
const query = computed(() => currentRoute.value.query)
const resetTokenParam = computed(() => {
  if (Object.keys(query.value).includes('token')) {
    return query.value.token as string
  }
  return null
})

const state = ref<ResetPasswordState>('REQUEST_TOKEN')
const resetToken = ref('')
const resetTokenMetadata = ref<ResetPasswordTokenDTO>()

function onTokenValidated(token: string, dto: ResetPasswordTokenDTO) {
  state.value = 'RESET_PASSWORD'
  resetToken.value = token
  resetTokenMetadata.value = dto
}

watch(resetTokenParam, (token) => {
  if (token !== null) {
    state.value = 'INPUT_TOKEN'
    resetToken.value = token
  }
}, { immediate: true })
</script>

<template>
  <div>
    <h2 class="text-center w-full mb-10 font-600 text-3xl">
      {{ t('pages.reset_password.title') }}
    </h2>

    <SlideTransitionContainer>
      <ResetPasswordRequestForm
        v-if="state === 'REQUEST_TOKEN'"
        class="w-125"
        @completed="state = 'INPUT_TOKEN'"
      />
      <ResetPasswordTokenForm
        v-if="state === 'INPUT_TOKEN'"
        :token="resetToken"
        class="w-125"
        @completed="onTokenValidated"
      />
      <ResetPasswordForm
        v-if="state === 'RESET_PASSWORD' && resetTokenMetadata"
        class="w-125"
        :token="resetToken"
        :token-metadata="resetTokenMetadata"
      />
    </SlideTransitionContainer>
  </div>
</template>
