<template>
  <div class="min-h-screen bg-gray-50 flex items-center justify-center">
    <div class="bg-white p-8 rounded-xl shadow w-full max-w-sm">
      <h1 class="text-2xl font-bold text-gray-800 mb-6">Tienda Admin</h1>

      <div class="mb-4">
        <label class="block text-sm font-medium text-gray-700 mb-1">Usuario</label>
        <input
          v-model="username"
          type="text"
          placeholder="admin"
          class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>
      <div class="mb-6">
        <label class="block text-sm font-medium text-gray-700 mb-1">Contraseña</label>
        <input
          v-model="password"
          type="password"
          placeholder="••••••"
          class="w-full border rounded-lg px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      <ErrorMessage v-if="error" :message="error" class="mb-4" />

      <button
        @click="handleLogin"
        class="w-full bg-blue-600 text-white py-2 rounded-lg hover:bg-blue-700 transition"
      >
        Ingresar
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import ErrorMessage from '@/components/ErrorMessage.vue'

const router = useRouter()
const auth = useAuthStore()
const username = ref('')
const password = ref('')
const error = ref(null)

async function handleLogin() {
  if (!username.value || !password.value) {
    error.value = 'Por favor ingresa usuario y contraseña'
    return
  }
  try {
    await auth.login(username.value, password.value)
    router.push('/products')
  } catch (err) {
    console.log(err);
    error.value = 'Credenciales incorrectas'
  }
}
</script>
