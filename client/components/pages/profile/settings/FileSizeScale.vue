<script setup lang="ts">
const model = defineModel<'BINARY' | 'DECIMAL'>({ required: true })
const { t } = useI18n()
</script>

<template>
  <VerticalActionPanel
    :header="t('pages.profile.settings.file_size_scale.title')"
    :description="t('pages.profile.settings.file_size_scale.description')"
  >
    <div class="flex flex-col gap-2 sm:flex-row sm:gap-3 sm:justify-center">
      <RadioCard
        v-model="model"
        value="BINARY"
        :title="t('pages.profile.settings.file_size_scale.binary.title')"
        :subtitle="t('pages.profile.settings.file_size_scale.binary.description')"
        radio-name="size-scale"
      >
        <p class="mt-3 text-lg text-center">
          {{ t('pages.profile.settings.file_size_scale.binary.summary') }}
        </p>
      </RadioCard>
      <RadioCard
        v-model="model"
        value="DECIMAL"
        :title="t('pages.profile.settings.file_size_scale.decimal.title')"
        :subtitle="t('pages.profile.settings.file_size_scale.decimal.description')"
        radio-name="size-scale"
      >
        <p class="mt-3 text-lg text-center">
          {{ t('pages.profile.settings.file_size_scale.decimal.summary') }}
        </p>
      </RadioCard>
    </div>
  </VerticalActionPanel>
</template>
