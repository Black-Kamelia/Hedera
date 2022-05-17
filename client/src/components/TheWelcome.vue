<script setup lang="ts">
import { useAxios } from "@vueuse/integrations/useAxios";
import api from "@/stores/axiosInstance";

const { data, isFinished, error } = useAxios("/api/hello", api);
</script>

<template>
  <div>
    <h1 class="text-3xl font-bold underline">Hello world !</h1>
    <div v-if="isFinished && !error">Received {{ data }}</div>
  </div>
</template>