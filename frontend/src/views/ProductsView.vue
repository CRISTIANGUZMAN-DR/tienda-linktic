<template>
  <div class="max-w-6xl mx-auto px-4 py-8">
    <div class="flex justify-between items-center mb-6">
      <h1 class="text-2xl font-bold text-gray-800">Productos</h1>
      <div class="flex gap-3">
        <button
          @click="router.push('/products/create')"
          class="bg-blue-600 text-white px-4 py-2 rounded-lg hover:bg-blue-700 transition text-sm"
        >
          + Crear Producto
        </button>
        <button
          @click="(auth.logout(), router.push('/login'))"
          class="text-sm text-gray-500 hover:text-gray-800"
        >
          Cerrar sesión
        </button>
      </div>
    </div>

    <!-- Filtros -->
    <div class="flex gap-3 mb-6 flex-wrap">
      <input
        v-model="search"
        @input="onFilter"
        placeholder="Buscar por nombre o SKU..."
        class="border rounded-lg px-3 py-2 w-64 focus:outline-none focus:ring-2 focus:ring-blue-500"
      />
      <select
        v-model="status"
        @change="onFilter"
        class="border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="">Todos los estados</option>
        <option value="ACTIVE">Activo</option>
        <option value="INACTIVE">Inactivo</option>
      </select>
      <select
        v-model="sortBy"
        @change="onFilter"
        class="border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
      >
        <option value="createdAt">Más recientes</option>
        <option value="price">Por precio</option>
      </select>
    </div>

    <!-- Estados -->
    <LoadingSpinner v-if="store.loading" />
    <ErrorMessage v-else-if="store.error" :message="store.error" :onRetry="loadProducts" />

    <div v-else-if="store.products.length === 0" class="text-center py-16 text-gray-400">
      No se encontraron productos
    </div>

    <div v-else>
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="product in store.products"
          :key="product.id"
          @click="router.push(`/products/${product.id}`)"
          class="bg-white border rounded-xl p-5 cursor-pointer hover:shadow-md transition"
        >
          <div class="flex justify-between items-start mb-2">
            <span class="text-xs font-mono text-gray-400">{{ product.sku }}</span>
            <span
              :class="
                product.status === 'ACTIVE'
                  ? 'bg-green-100 text-green-700'
                  : 'bg-gray-100 text-gray-500'
              "
              class="text-xs px-2 py-0.5 rounded-full font-medium"
            >
              {{ product.status }}
            </span>
          </div>
          <h2 class="font-semibold text-gray-800 mb-1">{{ product.name }}</h2>
          <p class="text-blue-600 font-bold">${{ product.price }}</p>
        </div>
      </div>

      <PaginationBar
        :page="store.pagination.page"
        :totalPages="store.pagination.totalPages"
        :totalElements="store.pagination.totalElements"
        @change="onPageChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useProductsStore } from '@/stores/products'
import { useAuthStore } from '@/stores/auth'
import LoadingSpinner from '@/components/LoadingSpinner.vue'
import ErrorMessage from '@/components/ErrorMessage.vue'
import PaginationBar from '@/components/PaginationBar.vue'

const router = useRouter()
const store = useProductsStore()
const auth = useAuthStore()

const search = ref('')
const status = ref('')
const sortBy = ref('createdAt')

function loadProducts(page = 0) {
  store.fetchProducts({
    page,
    size: 10,
    search: search.value || undefined,
    status: status.value || undefined,
    sortBy: sortBy.value,
    sortDir: 'desc',
  })
}

function onFilter() {
  loadProducts(0)
}
function onPageChange(page) {
  loadProducts(page)
}
onMounted(() => loadProducts())
</script>
