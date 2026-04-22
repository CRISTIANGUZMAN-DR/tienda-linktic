import { productsApi } from './axios'

export const getProducts = (params) => productsApi.get('/api/products', { params })

export const getProduct = (id) => productsApi.get(`/api/products/${id}`)

export const createProduct = (data) => productsApi.post('/api/products', data)
