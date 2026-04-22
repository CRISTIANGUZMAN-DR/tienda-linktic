<template>
  <div class="max-w-2xl mx-auto px-4 py-8">
    <button
      @click="router.back()"
      class="text-blue-600 hover:underline mb-6 flex items-center gap-1"
    >
      ← Volver
    </button>

    <LoadingSpinner v-if="productsStore.loading" />
    <ErrorMessage v-else-if="productsStore.error" :message="productsStore.error" />

    <div v-else-if="product" class="bg-white rounded-xl border p-6">
      <div class="flex justify-between items-start mb-4">
        <div>
          <span class="text-xs font-mono text-gray-400">{{ product.sku }}</span>
          <h1 class="text-2xl font-bold text-gray-800 mt-1">{{ product.name }}</h1>
        </div>
        <span
          :class="
            product.status === 'ACTIVE'
              ? 'bg-green-100 text-green-700'
              : 'bg-gray-100 text-gray-500'
          "
          class="text-sm px-3 py-1 rounded-full font-medium"
        >
          {{ product.status }}
        </span>
      </div>

      <p class="text-3xl font-bold text-blue-600 mb-6">${{ product.price }}</p>

      <!-- Inventario -->
      <div class="bg-gray-50 rounded-lg p-4 mb-6">
        <h2 class="font-semibold text-gray-700 mb-2">Inventario disponible</h2>
        <LoadingSpinner v-if="inventoryStore.loading" />
        <ErrorMessage
          v-else-if="inventoryStore.error"
          :message="inventoryStore.error"
          :onRetry="() => inventoryStore.fetchInventory(product.id)"
        />
        <p v-else class="text-2xl font-bold text-gray-800">
          {{ inventoryStore.inventory?.available ?? 0 }} unidades
        </p>
      </div>
      <div class="flex justify-end mb-4">
        <button
          @click="router.push(`/inventory/set/${product.id}`)"
          class="bg-green-600 text-white px-4 py-2 rounded-lg hover:bg-green-700 transition text-sm"
        >
          📦 Actualizar Stock
        </button>
      </div>

      <!-- Compra -->
      <div class="border-t pt-6">
        <h2 class="font-semibold text-gray-700 mb-3">Realizar compra</h2>

        <div class="flex gap-3 items-center mb-4">
          <input
            v-model.number="quantity"
            type="number"
            min="1"
            placeholder="Cantidad"
            class="border rounded-lg px-3 py-2 w-32 focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
          <button
            @click="handlePurchase"
            :disabled="inventoryStore.loading || quantity < 1"
            class="bg-blue-600 text-white px-6 py-2 rounded-lg hover:bg-blue-700 disabled:opacity-50 transition"
          >
            Comprar
          </button>
        </div>

        <!-- Feedback compra -->
        <div
          v-if="inventoryStore.purchaseResult"
          class="bg-green-50 border border-green-200 rounded-lg p-4"
        >
          ✅ Compra exitosa — Stock restante: {{ inventoryStore.purchaseResult.remainingStock }}
        </div>
        <ErrorMessage v-else-if="inventoryStore.error" :message="inventoryStore.error" />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useProductsStore } from '@/stores/products'
import { useInventoryStore } from '@/stores/inventory'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import ErrorMessage from '@/components/ErrorMessage.vue'

const route = useRoute()
const router = useRouter()
const productsStore = useProductsStore()
const inventoryStore = useInventoryStore()

const quantity = ref(1)
const product = computed(() => productsStore.currentProduct)

async function handlePurchase() {
  await inventoryStore.makePurchase(product.value.id, quantity.value)
}

onMounted(async () => {
  await productsStore.fetchProduct(route.params.id)
  if (productsStore.currentProduct) {
    await inventoryStore.fetchInventory(route.params.id)
  }
})
</script>
