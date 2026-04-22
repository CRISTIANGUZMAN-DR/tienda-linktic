import { setActivePinia, createPinia } from 'pinia'
import { beforeEach, describe, it, expect, vi } from 'vitest'
import { useInventoryStore } from '../inventory'
import * as inventoryApi from '@/api/inventory'

vi.mock('@/api/inventory')

describe('Inventory Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchInventory guarda el inventario', async () => {
    inventoryApi.getInventory.mockResolvedValue({
      data: { productId: '1', available: 50, reserved: 0 },
    })

    const store = useInventoryStore()
    await store.fetchInventory('1')

    expect(store.inventory.available).toBe(50)
    expect(store.error).toBeNull()
  })

  it('makePurchase exitosa actualiza el stock local', async () => {
    inventoryApi.purchase.mockResolvedValue({
      data: { status: 'SUCCESS', productId: '1', quantityBought: 2, remainingStock: 48 },
    })

    const store = useInventoryStore()
    store.inventory = { available: 50, reserved: 0 }
    await store.makePurchase('1', 2)

    expect(store.purchaseResult.remainingStock).toBe(48)
    expect(store.inventory.available).toBe(48)
  })

  it('makePurchase guarda error cuando hay stock insuficiente', async () => {
    inventoryApi.purchase.mockRejectedValue({
      userMessage: 'Conflicto: Insufficient stock. Available: 0, requested: 2',
    })

    const store = useInventoryStore()
    await store.makePurchase('1', 2)

    expect(store.error).toContain('Conflicto')
    expect(store.purchaseResult).toBeNull()
  })
})
