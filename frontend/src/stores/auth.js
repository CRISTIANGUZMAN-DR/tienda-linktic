import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import axios from 'axios'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('token') || null)
  const isAuthenticated = computed(() => !!token.value)

  async function login(username, password) {
    const res = await axios.post('http://localhost:8081/auth/login', {
      username,
      password,
    })
    console.log('Token recibido:', res.data)
    token.value = res.data.token
    localStorage.setItem('token', res.data.token)
  }

  function logout() {
    token.value = null
    localStorage.removeItem('token')
  }

  return { token, isAuthenticated, login, logout }
})
