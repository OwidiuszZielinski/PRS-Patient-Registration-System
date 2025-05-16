/**
 * main.js
 *
 * Bootstraps Vuetify and other plugins then mounts the App
 */

// Composables
import { createApp } from 'vue'
import App from './App.vue'

// Plugins
import { registerPlugins } from '@/plugins'

// Flatpickr
import Flatpickr from 'vue-flatpickr-component'
import 'flatpickr/dist/flatpickr.css'
import { Polish } from 'flatpickr/dist/l10n/pl.js'
import { VDateInput } from 'vuetify/labs/VDateInput'
import { createVuetify } from 'vuetify'
import { pl } from 'vuetify/locale'

const vuetify = createVuetify({
  locale: {
    locale: 'pl',                          // ustawiamy PL
    fallback: 'en',                        // (opcjonalnie) jeśli brak tłumaczenia
    messages: { pl },                      // przekazujemy obiekt
  },
})

const app = createApp(App)
  .use(vuetify)

// zarejestruj globalnie Flatpickra
app.component('VDateInput', VDateInput)

// tutaj rejestrujesz inne pluginy (np. Vuetify)
registerPlugins(app)

// mount głównej appki
app.mount('#app')
