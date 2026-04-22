<template>
  <div class="max-w-2xl mx-auto px-4 py-8">
    <button
      @click="router.back()"
      class="text-blue-600 hover:underline mb-6 flex items-center gap-1"
    >
      ← Volver
    </button>

    <div class="bg-white rounded-xl border p-6">
      <h1 class="text-2xl font-bold text-gray-800 mb-6">Crear Producto</h1>

      <div class="flex flex-col gap-4">
        <!-- SKU -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">SKU *</label>
          <input
            v-model="form.sku"
            type="text"
            placeholder="PROD-001"
            class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            :class="errors.sku ? 'border-red-400' : ''"
          />
          <p v-if="errors.sku" class="text-red-500 text-sm mt-1">{{ errors.sku }}</p>
        </div>

        <!-- Nombre -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Nombre *</label>
          <input
            v-model="form.name"
            type="text"
            placeholder="Camiseta Azul"
            class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            :class="errors.name ? 'border-red-400' : ''"
          />
          <p v-if="errors.name" class="text-red-500 text-sm mt-1">{{ errors.name }}</p>
        </div>

        <!-- Precio -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Precio *</label>
          <input
            v-model.number="form.price"
            type="number"
            min="0"
            step="0.01"
            placeholder="29.99"
            class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
            :class="errors.price ? 'border-red-400' : ''"
          />
          <p v-if="errors.price" class="text-red-500 text-sm mt-1">{{ errors.price }}</p>
        </div>

        <!-- Status -->
        <div>
          <label class="block text-sm font-medium text-gray-700 mb-1">Estado</label>
          <select
            v-model="form.status"
            class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
          >
            <option value="ACTIVE">Activo</option>
            <option value="INACTIVE">Inactivo</option>
          </select>
        </div>

        <!-- Error global -->
        <ErrorMessage v-if="error" :message="error" />

        <!-- Éxito -->
        <div
          v-if="success"
          class="bg-green-50 border border-green-200 rounded-lg p-4 text-green-700"
        >
          ✅ Producto creado exitosamente
          <button @click="router.push('/products')" class="ml-2 underline text-green-800">
            Ver productos
          </button>
        </div>

        <button
          @click="handleSubmit"
          :disabled="loading"
          class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
        >
          {{ loading ? 'Creando...' : 'Crear Producto' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { createProduct } from '@/api/products'
import ErrorMessage from '@/components/ErrorMessage.vue'

const router = useRouter()

const form = ref({ sku: '', name: '', price: null, status: 'ACTIVE' })
const errors = ref({})
const error = ref(null)
const loading = ref(false)
const success = ref(false)

function validate() {
  errors.value = {}
  if (!form.value.sku) errors.value.sku = 'El SKU es requerido'
  if (!form.value.name) errors.value.name = 'El nombre es requerido'
  if (form.value.price === null || form.value.price < 0)
    errors.value.price = 'El precio debe ser mayor o igual a 0'
  return Object.keys(errors.value).length === 0
}

async function handleSubmit() {
  if (!validate()) return
  loading.value = true
  error.value = null
  success.value = false
  try {
    await createProduct(form.value)
    success.value = true
    form.value = { sku: '', name: '', price: null, status: 'ACTIVE' }
  } catch (err) {
    error.value = err.userMessage
  } finally {
    loading.value = false
  }
}
</script>
