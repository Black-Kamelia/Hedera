<script setup lang="ts">
const { t } = useI18n()

const STATES_ORDER = ['REQUEST_TOKEN', 'INPUT_TOKEN', 'RESET_PASSWORD'] as const
type ResetPasswordState = typeof STATES_ORDER[number]

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
const stateHistory = useRefHistory(state, { capacity: 2 })

const stateTransition = computed(() => {
  const history = stateHistory.history.value
  if (history.length < 2) return 'LEFT'
  const [next, prev] = history
  return STATES_ORDER.indexOf(prev.snapshot) < STATES_ORDER.indexOf(next.snapshot) ? 'LEFT' : 'RIGHT'
})

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
  } else {
    resetToken.value = ''
  }
}, { immediate: true })
</script>

<template>
  <div>
    <h2 class="text-center w-full mb-10 font-600 text-3xl">
      {{ t('pages.reset_password.title') }}
    </h2>

    <SlideTransitionContainer :direction="stateTransition" gap="2.5em">
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
        @back="state = 'REQUEST_TOKEN'"
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
