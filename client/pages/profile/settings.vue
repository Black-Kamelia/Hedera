<script setup lang="ts">
const settings = useUserSettings()
const axios = useAxiosFactory()
const { t, m } = useI18n()
const toast = useToast()

const fileSizeScale = computed(() => settings.filesSizeScale)
const defaultFileVisibility = computed(() => settings.defaultFileVisibility)
const autoRemoveFiles = computed(() => settings.autoRemoveFiles)
const preferredDateTimeFormat = computed(() => ({
  dateStyle: settings.preferredDateStyle as DateTimeStyle,
  timeStyle: settings.preferredTimeStyle as DateTimeStyle,
}))
const preferredLocale = computed(() => settings.preferredLocale)

// Local settings
const color = useColorMode()
const animations = useLocalStorage('animations', true)

function patchSettings(newSettings: Partial<UserSettings>) {
  return axios().patch<UserSettings>('/users/settings', newSettings)
    .then((response) => {
      settings.updateSettings(response.data)
      return response
    })
    .catch((error) => {
      if (!error.response) {
        toast.add({
          severity: 'error',
          summary: t('errors.unknown'),
          detail: { text: t('errors.network') },
          life: 5000,
        })
        return error
      }
      toast.add({
        severity: 'error',
        summary: t('error'), // TODO: get error title from backend
        detail: { text: m(error) },
        life: 5000,
      })
      return error
    })
}

provide(UserSettingsKey, { patchSettings })
</script>

<template>
  <div class="flex flex-col gap-3">
    <h1 class="text-2xl">
      {{ t('pages.profile.settings.headers.files_upload') }}
    </h1>
    <DefaultFileVisibility :value="defaultFileVisibility" />
    <AutoRemoveFiles :value="autoRemoveFiles" />

    <h1 class="text-2xl mt-6">
      {{ t('pages.profile.settings.headers.display_and_animations') }}
    </h1>
    <FileSizeScale :value="fileSizeScale" />
    <EnableAnimations v-model="animations" />
    <PreferredTheme v-model="color.preference" />
    <PreferredDateTimeFormat :value="preferredDateTimeFormat" />
    <PreferredLocale :value="preferredLocale" />

    <div class="flex items-center justify-center flex-col mt-8 p-4 color-[--text-color-secondary]">
      <div class="logo w-20em h-4em" />
      <p>Version 0.0.1</p>
      <small><a href="https://black-kamelia.com/">Black-Kamelia</a> • <a href="https://git.black-kamelia.com/Hedera">GitHub</a></small>
    </div>
  </div>
</template>

<style scoped>
.logo {
  background-color: var(--text-color-secondary);
  -webkit-mask: url(/assets/img/Hedera.svg) no-repeat center;
  mask: url(/assets/img/Hedera.svg) no-repeat center;
}
</style>
