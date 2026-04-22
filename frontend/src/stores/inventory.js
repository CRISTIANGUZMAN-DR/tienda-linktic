import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getInventory, purchase } from '@/api/inventory'

export const useInventoryStore = defineStore('inventory', () => {
  const inventory = ref(null)
  const loading = ref(false)
  const error = ref(null)
  const purchaseResult = ref(null)

  async function fetchInventory(productId) {
    loading.value = true
    error.value = null
    try {
      const res = await getInventory(productId)
      inventory.value = res.data
    } catch (err) {
      error.value = err.userMessage
    } finally {
      loading.value = false
    }
  }

  async function makePurchase(productId, quantity) {
    loading.value = true
    error.value = null
    purchaseResult.value = null
    try {
      const res = await purchase({ productId, quantity })
      purchaseResult.value = res.data
      // Actualizar stock local inmediatamente
      if (inventory.value) {
        inventory.value.available = res.data.remainingStock
      }
    } catch (err) {
      error.value = err.userMessage
    } finally {
      loading.value = false
    }
  }

  return { inventory, loading, error, purchaseResult, fetchInventory, makePurchase }
})
