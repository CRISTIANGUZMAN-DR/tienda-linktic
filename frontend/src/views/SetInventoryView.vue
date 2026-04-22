<template>
  <div class="max-w-2xl mx-auto px-4 py-8">
    <button
      @click="router.back()"
      class="text-blue-600 hover:underline mb-6 flex items-center gap-1"
    >
      ← Volver
    </button>

    <div class="bg-white rounded-xl border p-6">
      <h1 class="text-2xl font-bold text-gray-800 mb-6">Setear Inventario</h1>

      <div class="flex flex-col gap-4">
        <!-- Product ID -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">ID del Producto</label>
          <input
            v-model="form.productId"
            type="text"
            :readonly="!!route.params.productId"
            :class="[
              'w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500',
              route.params.productId ? 'bg-gray-50 text-gray-500' : '',
              errors.productId ? 'border-red-400' : '',
            ]"
          />
          <p v-if="errors.productId" class="text-red-500 text-sm mt-1">{{ errors.productId }}</p>
        </div>
        <!-- Stock disponible -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Stock disponible *</label>
          <input
            v-model.number="form.available"
            type="number"
            min="0"
            placeholder="100"
            class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            :class="errors.available ? 'border-red-400' : ''"
          />
          <p v-if="errors.available" class="text-red-500 text-sm mt-1">{{ errors.available }}</p>
        </div>

        <!-- Error global -->
        <ErrorMessage v-if="error" :message="error" />

        <!-- Éxito -->
        <div
          v-if="success"
          class="bg-green-50 border border-green-200 rounded-lg p-4 text-green-700"
        >
          ✅ Inventario actualizado — Stock disponible: {{ result?.available }}
        </div>

        <button
          @click="handleSubmit"
          :disabled="loading"
          class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
        >
          {{ loading ? 'Guardando...' : 'Guardar Inventario' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { setInventory } from '@/api/inventory'
import ErrorMessage from '@/components/ErrorMessage.vue'

const router = useRouter()
const route = useRoute()

const form = ref({ productId: '', available: null })
const errors = ref({})
const error = ref(null)
const loading = ref(false)
const success = ref(false)
const result = ref(null)

onMounted(() => {
  if (route.params.productId) {
    form.value.productId = route.params.productId
  }
})

function validate() {
  errors.value = {}
  if (!form.value.productId) errors.value.productId = 'El ID del producto es requerido'
  if (form.value.available === null || form.value.available < 0)
    errors.value.available = 'El stock debe ser mayor o igual a 0'
  return Object.keys(errors.value).length === 0
}

async function handleSubmit() {
  if (!validate()) return
  loading.value = true
  error.value = null
  success.value = false
  try {
    const res = await setInventory(form.value)
    result.value = res.data
    success.value = true
  } catch (err) {
    error.value = err.userMessage
  } finally {
    loading.value = false
  }
}
</script>
