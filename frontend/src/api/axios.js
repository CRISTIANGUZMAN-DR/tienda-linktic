import axios from 'axios'
import { useAuthStore } from '@/stores/auth'

const productsApi = axios.create({
  baseURL: 'http://localhost:8081',
  timeout: 5000,
})

const inventoryApi = axios.create({
  baseURL: 'http://localhost:8082',
  timeout: 5000,
})

// Interceptor: agrega el JWT a cada request
const authInterceptor = (config) => {
  const auth = useAuthStore()
  if (auth.token) {
    config.headers.Authorization = `Bearer ${auth.token}`
  }
  return config
}

productsApi.interceptors.request.use(authInterceptor)
inventoryApi.interceptors.request.use(authInterceptor)

// Interceptor: manejo de errores global
const errorInterceptor = (error) => {
  if (error.code === 'ECONNABORTED') {
    return Promise.reject({ userMessage: 'El servidor tardó demasiado. Intenta de nuevo.' })
  }
  if (!error.response) {
    return Promise.reject({ userMessage: 'No se pudo conectar al servidor.' })
  }
  const status = error.response.status
  const message = error.response.data?.message || 'Error desconocido'

  const messages = {
    404: `No encontrado: ${message}`,
    409: `Conflicto: ${message}`,
    422: `Datos inválidos: ${message}`,
    503: `Servicio no disponible: ${message}`,
  }

  return Promise.reject({
    userMessage: messages[status] || `Error ${status}: ${message}`,
    status,
    raw: error.response.data,
  })
}

productsApi.interceptors.response.use(null, errorInterceptor)
inventoryApi.interceptors.response.use(null, errorInterceptor)

export { productsApi, inventoryApi }
