import { setActivePinia, createPinia } from 'pinia'
import { beforeEach, describe, it, expect, vi } from 'vitest'
import { useProductsStore } from '../products'
import * as productsApi from '@/api/products'

vi.mock('@/api/products')

describe('Products Store', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchProducts guarda productos en el store', async () => {
    productsApi.getProducts.mockResolvedValue({
      data: {
        content: [{ id: '1', sku: 'P-001', name: 'Camiseta', price: 29.99, status: 'ACTIVE' }],
        number: 0,
        size: 10,
        totalPages: 1,
        totalElements: 1,
      },
    })

    const store = useProductsStore()
    await store.fetchProducts()

    expect(store.products).toHaveLength(1)
    expect(store.products[0].sku).toBe('P-001')
    expect(store.loading).toBe(false)
    expect(store.error).toBeNull()
  })

  it('fetchProducts guarda error cuando la API falla', async () => {
    productsApi.getProducts.mockRejectedValue({ userMessage: 'No se pudo conectar al servidor.' })

    const store = useProductsStore()
    await store.fetchProducts()

    expect(store.products).toHaveLength(0)
    expect(store.error).toBe('No se pudo conectar al servidor.')
  })

  it('fetchProduct guarda el producto actual', async () => {
    productsApi.getProduct.mockResolvedValue({
      data: { id: '1', sku: 'P-001', name: 'Camiseta', price: 29.99, status: 'ACTIVE' },
    })

    const store = useProductsStore()
    await store.fetchProduct('1')

    expect(store.currentProduct.sku).toBe('P-001')
  })

  it('usa cache si los datos no expiraron', async () => {
    productsApi.getProducts.mockResolvedValue({
      data: {
        content: [{ id: '1', sku: 'P-001', name: 'Camiseta', price: 29.99, status: 'ACTIVE' }],
        number: 0,
        size: 10,
        totalPages: 1,
        totalElements: 1,
      },
    })

    const store = useProductsStore()
    await store.fetchProducts({ page: 0 })
    await store.fetchProducts({ page: 0 }) // segunda llamada — debe usar caché

    expect(productsApi.getProducts).toHaveBeenCalledTimes(1)
  })
})
