<script setup lang="ts">
const { t } = useI18n()

usePageName(() => t('pages.login.title'))
definePageMeta({
  layout: 'centercard',
  middleware: ['auth'],
})
</script>

<template>
  <div class="text-center mb-10">
    <h1 class="font-600 text-5xl mb-1">
      {{ t('app_name') }}
    </h1>
    <h2 class="font-600 text-3xl mb-3">
      {{ t('pages.change_password.title') }}
    </h2>
  </div>

  <PMessage icon="i-tabler-info-circle-filled" :closable="false">
    Vous devez modifier votre mot de passe.
  </PMessage>

  <PasswordEditionForm />
</template>
