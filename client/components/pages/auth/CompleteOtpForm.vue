<script lang="ts" setup>
const { t } = useI18n()
</script>

<template>
  <form class="flex flex-col items-center">
    <div class="mb-6">
      <InputOTP />
    </div>

    <div class="mt-6 w-full flex flex-col-reverse sm:flex-row gap-3">
      <PButton :label="t('global.logout')" class="w-full" outlined />
      <PButton :label="t('forms.submit')" class="w-full" type="submit" />
    </div>
  </form>
</template>
