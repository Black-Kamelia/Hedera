<script lang="ts" setup>
const dark = $isDark
</script>

<template>
  <div center h-screen>
    <div class="absolute top-10 right-10 flex flex-row gap-2">
      <select class="px-2 py-1 rounded-lg" ::="$i18n.locale">
        <option value="en">
          en
        </option>
        <option value="fr">
          fr
        </option>
      </select>
      <button class="icon-btn text-8" @click="toggleDark()">
        <div i="tabler-sun dark:tabler-moon" />
      </button>
    </div>
    <p class="absolute top-10 left-10 text-8 font-text">
      {{ dark ? $t('theme.dark') : $t('theme.light') }}
    </p>

    <h1 font="display bold" text="30 hover:violet-700" transition>
      {{ $t('main.title') }}
    </h1>
  </div>
</template>
