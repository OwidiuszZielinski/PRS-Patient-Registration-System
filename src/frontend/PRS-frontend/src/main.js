/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App
 */

// Composables
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'

// Plugins
import { registerPlugins } from '@/plugins'

// Flatpickr
import Flatpickr from 'vue-flatpickr-component'
import 'flatpickr/dist/flatpickr.css'
import { Polish } from 'flatpickr/dist/l10n/pl.js'
import { VDateInput } from 'vuetify/labs/VDateInput'
import { createVuetify } from 'vuetify'
import { pl } from 'vuetify/locale'
import { VTimePicker } from 'vuetify/labs/VTimePicker'
import { ref } from 'vue'

const vuetify = createVuetify({
  locale: {
    locale: 'pl',                          // ustawiamy PL
    fallback: 'en',                        // (opcjonalnie) jeśli brak tłumaczenia
    messages: { pl },                      // przekazujemy obiekt
  },
    components: {
        VTimePicker,
      },
})

 const time = ref(null)
const menu2 = ref(false)
const moda12 = ref(false)

// Globalna konfiguracja axios
axios.defaults.baseURL = 'http://localhost:8080'

// Dodaj token JWT do wszystkich żądań, jeśli istnieje
const token = localStorage.getItem('jwt_token')
if (token) {
  axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

// Interceptor do obsługi błędów autoryzacji
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Token wygasł lub jest nieprawidłowy
      localStorage.removeItem('jwt_token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      delete axios.defaults.headers.common['Authorization']
      
      // Przekieruj na stronę logowania
      if (router.currentRoute.value.path !== '/login') {
        router.push('/login')
      }
    }
    return Promise.reject(error)
  }
)

const app = createApp(App)
  .use(vuetify)
  .use(router)

// zarejestruj globalnie Flatpickra
app.component('VDateInput', VDateInput)

// tutaj rejestrujesz inne pluginy (np. Vuetify)
registerPlugins(app)

// mount głównej appki
app.mount('#app')
