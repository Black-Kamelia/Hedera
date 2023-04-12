<script lang="ts" setup>
import InputOTP from '~/components/input/InputOTP.vue'

const { toggle } = useDark()
const { locale, t } = useI18n()

const [, { logout }] = useAuth()

const digits = createEmptyOTP()
function onCompleted(digits: Nullable<number>[]) {
  console.log(digits) // eslint-disable-line no-console
}
</script>

<template>
  <div flex-center h-screen>
    <div class="absolute top-10 right-10 flex flex-row gap-2">
      <PButton @click="logout()">
        <p>{{ t('global.logout') }}</p>
      </PButton>
      <PButton @click="navigateTo('/login')">
        <p>{{ t('global.login') }}</p>
      </PButton>
      <select v-model="locale" class="px-2 py-1 rounded-lg">
        <option value="en">
          en
        </option>
        <option value="fr">
          fr
        </option>
      </select>
      <button class="icon-btn text-8" @click="toggle()">
        <div i="tabler-sun dark:tabler-moon" />
      </button>
    </div>
    <p class="absolute top-10 left-10 text-8 font-text">
      {{ $colorMode.value === 'dark' ? t('theme.dark') : t('theme.light') }}
    </p>

    <InputOTP v-model="digits" @completed="onCompleted" />
    <div class="flex flex-row">
      <span v-for="(digit, index) in digits" :key="index">{{ digit ?? 'X' }}</span>
    </div>
  </div>
</template>
