<script setup lang="ts">
definePageMeta({
  layout: 'centercard',
})

useHead({
  title: 'Sign in • Hedera',
})
</script>

<template>
  <div class="text-center mb-5">
    <h1 class="font-extrabold text-4xl mb-3">
      Sign in
    </h1>
  </div>

  <div>
    <label for="email1" class="block font-900 font-medium mb-2">Email</label>
    <PInputText id="email1" type="text" placeholder="john.doe@example.fr" class="w-full mb-3" />

    <label for="password1" class="block font-900 font-medium mb-2">Password</label>
    <PInputText id="password1" type="password" placeholder="••••••••••••••••" class="w-full mb-3" />

    <div class="flex flex-row items-center justify-between mb-6 w-100%">
      <div v-tooltip.bottom="{ value: 'Coming soon', class: 'p-0' }" class="flex flex-row items-center">
        <PCheckbox id="rememberme1" :binary="true" disabled class="mr-2" />
        <label for="rememberme1" class="text-gray">Remember me</label>
      </div>
      <a class="font-medium no-underline ml-2 text-blue-500 text-right cursor-pointer">Forgot password?</a>
    </div>

    <PButton label="Sign In" icon="pi pi-user" class="w-full" />
  </div>
</template>
