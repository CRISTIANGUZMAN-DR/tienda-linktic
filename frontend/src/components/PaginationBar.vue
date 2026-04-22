<template>
  <div class="flex items-center justify-between mt-6">
    <p class="text-sm text-gray-500">{{ totalElements }} productos en total</p>
    <div class="flex gap-2">
      <button
        @click="$emit('change', page - 1)"
        :disabled="page === 0"
        class="px-3 py-1 rounded border disabled:opacity-40 hover:bg-gray-100"
      >
        ← Anterior
      </button>
      <span class="px-3 py-1 text-sm text-gray-600"> {{ page + 1 }} / {{ totalPages }} </span>
      <button
        @click="$emit('change', page + 1)"
        :disabled="page >= totalPages - 1"
        class="px-3 py-1 rounded border disabled:opacity-40 hover:bg-gray-100"
      >
        Siguiente →
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({ page: Number, totalPages: Number, totalElements: Number })
defineEmits(['change'])
</script>
