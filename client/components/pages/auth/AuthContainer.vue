<script lang="ts" setup>
import CompleteOtpForm from '~/components/pages/auth/CompleteOtpForm.vue'

const {
  initialState = LoginState,
} = defineProps<{
  initialState?: AuthState
}>()

const { t } = useI18n()

const state = ref<AuthState>(initialState)

const stateHistory = useRefHistory(state, { capacity: 2 })

const stateTransition = computed(() => {
  const history = stateHistory.history.value
  if (history.length < 2) return 'LEFT'
  const [next, prev] = history
  return prev.snapshot.index < next.snapshot.index ? 'LEFT' : 'RIGHT'
})

watch(state, (value) => {
  if (value.route) history.pushState({ ...history.state }, '', value.route)
})
</script>

<template>
  <div>
    <div class="absolute top-3 left-3 flex gap-3">
      <PButton @click="state = RegisterState">
        Register
      </PButton>
      <PButton @click="state = ResetPasswordState">
        Reset PWD
      </PButton>
      <PButton @click="state = LoginState">
        Login
      </PButton>
      <PButton @click="state = CompleteOTPState">
        Complete OTP
      </PButton>
      <PButton @click="state = ChangePasswordState">
        Change PWD
      </PButton>
    </div>

    <div class="text-center mb-10">
      <h1 class="font-600 text-5xl mb-1">
        {{ t('app_name') }}
      </h1>
      <div class="relative w-full">
        <SlideTransitionContainer :direction="stateTransition" gap="2.5em">
          <h2 :key="state.name" class="w-full font-600 text-3xl mb-3">
            {{ t(state.subtitleKey) }}
          </h2>
        </SlideTransitionContainer>
      </div>
    </div>

    <SlideTransitionContainer :direction="stateTransition" animate-width gap="2.5em">
      <RegisterForm
        v-if="state.name === 'REGISTER'"
        v-model:state="state"
        class="w-175"
      />
      <ResetPasswordForm
        v-else-if="state.name === 'RESET_PASSWORD'"
        v-model:state="state"
        class="w-125"
      />
      <LoginForm
        v-else-if="state.name === 'LOGIN'"
        v-model:state="state"
        class="w-125"
      />
      <CompleteOtpForm
        v-else-if="state.name === 'COMPLETE_OTP'"
        class="w-125"
      />
      <PasswordEditionForm
        v-else-if="state.name === 'CHANGE_PASSWORD'"
        class="w-125"
      />
    </SlideTransitionContainer>
  </div>
</template>
