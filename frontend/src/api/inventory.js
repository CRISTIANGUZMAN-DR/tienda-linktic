import { inventoryApi } from './axios'

export const getInventory = (productId) => inventoryApi.get(`/api/inventory/${productId}`)

export const purchase = (data) =>
  inventoryApi.post('/api/purchases', data, {
    headers: { 'Idempotency-Key': crypto.randomUUID() },
  })

export const setInventory = (data) => inventoryApi.post('/api/inventory', data)
