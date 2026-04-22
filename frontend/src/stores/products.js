import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getProducts, getProduct } from '@/api/products'

const CACHE_TTL = 60 * 1000 // 1 minuto

export const useProductsStore = defineStore('products', () => {
  const products = ref([])
  const currentProduct = ref(null)
  const pagination = ref({ page: 0, size: 10, totalPages: 0, totalElements: 0 })
  const loading = ref(false)
  const error = ref(null)

  // Caché simple: { [cacheKey]: { data, timestamp } }
  const cache = ref({})

  async function fetchProducts(params = {}) {
    const cacheKey = JSON.stringify(params)
    const cached = cache.value[cacheKey]

    // Devuelve caché si no expiró
    if (cached && Date.now() - cached.timestamp < CACHE_TTL) {
      products.value = cached.data.content
      pagination.value = {
        page: cached.data.number,
        size: cached.data.size,
        totalPages: cached.data.totalPages,
        totalElements: cached.data.totalElements,
      }
      return
    }

    loading.value = true
    error.value = null
    try {
      const res = await getProducts(params)
      products.value = res.data.content
      pagination.value = {
        page: res.data.number,
        size: res.data.size,
        totalPages: res.data.totalPages,
        totalElements: res.data.totalElements,
      }
      cache.value[cacheKey] = { data: res.data, timestamp: Date.now() }
    } catch (err) {
      error.value = err.userMessage
    } finally {
      loading.value = false
    }
  }

  async function fetchProduct(id) {
    loading.value = true
    error.value = null
    currentProduct.value = null
    try {
      const res = await getProduct(id)
      currentProduct.value = res.data
    } catch (err) {
      error.value = err.userMessage
    } finally {
      loading.value = false
    }
  }

  return { products, currentProduct, pagination, loading, error, fetchProducts, fetchProduct }
})
