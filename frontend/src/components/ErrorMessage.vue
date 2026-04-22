<template>
  <div class="bg-red-50 border border-red-200 rounded-lg p-4 flex items-start gap-3">
    <span class="text-red-500 text-xl">⚠️</span>
    <div>
      <p class="text-red-700 font-medium">{{ message }}</p>
      <button
        v-if="onRetry"
        @click="onRetry"
        class="mt-2 text-sm text-red-600 underline hover:text-red-800"
      >
        Reintentar
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  message: String,
  onRetry: Function,
})
</script>
