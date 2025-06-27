import { createRouter, createWebHistory } from 'vue-router'
import { createApp, h, ref, onMounted } from 'vue'
import axios from 'axios'
import { store } from '@/store.js'

import Home from '@/pages/Home.vue'
import WaitingRoom from '@/pages/WaitingRoom.vue'
import DoctorPanel from '@/pages/DoctorPanel.vue'
import RegistryPanel from '@/pages/RegistryPanel.vue'
import LogoutPanel from '@/pages/LogoutPanel.vue'
import PatientVisitPanel from '@/pages/PatientVisitPanel.vue'
import Register from '@/pages/Register.vue'
import RedirectBanner from '@/components/RedirectBanner.vue'
import PaymentStatus from '@/components/PaymentStatus.vue'

// Definicja uprawnień dla każdej ścieżki
const routePermissions = {
  '/': ['ADMIN', 'DOCTOR', 'PATIENT', 'WAITING_ROOM'],
  '/waiting-room': ['ADMIN', 'DOCTOR', 'WAITING_ROOM'],
  '/doctor': ['ADMIN', 'DOCTOR'],
  '/registry': ['ADMIN'],
  '/logout': ['ADMIN', 'DOCTOR', 'PATIENT', 'WAITING_ROOM'],
  '/patient-view': ['ADMIN', 'PATIENT'],
  '/register': ['ADMIN', 'DOCTOR', 'PATIENT', 'WAITING_ROOM'],
  '/login': ['ADMIN', 'DOCTOR', 'PATIENT', 'WAITING_ROOM'],
  '/payment-status': ['ADMIN', 'DOCTOR', 'PATIENT', 'WAITING_ROOM']
}

const routes = [
  { path: '/', component: Home },
  { path: '/waiting-room', component: WaitingRoom },
  { path: '/doctor', component: DoctorPanel },
  { path: '/registry', component: RegistryPanel },
  { path: '/logout', component: LogoutPanel },
  { path: '/patient-view', component: PatientVisitPanel },
  { path: '/register', component: Register },
  { path: '/login', component: () => import('@/pages/Login.vue') },
  { path: '/payment-status', component: PaymentStatus },
  // Catch-all route for undefined paths
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    next()
    return
  }

  const token = store.user.token || localStorage.getItem('jwt_token')
  if (!token) {
    mountCountdownBanner(
      'Need authentication.',
      3,
      3000,
      '/login'
    )
    return
  }

  try {
    const response = await axios.get('http://localhost:8080/api/v1/auth/validate', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    })
    
    if (response.data) {
      axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
      
      // Sprawdzenie uprawnień na podstawie roli
      const userRole = store.user.role || localStorage.getItem('role')
      const allowedRoles = routePermissions[to.path]
      
      // Jeśli ścieżka nie jest zdefiniowana w uprawnieniach, pozwól na dostęp
      if (allowedRoles && !allowedRoles.includes(userRole)) {
        // Użytkownik nie ma uprawnień do tej ścieżki
        mountCountdownBanner(
          `Access denied. This resource is not available for your role (${userRole}). Redirecting to home page...`,
          5,
          5000,
          '/'
        )
        return
      }
      
      next()
    } else {
      localStorage.removeItem('jwt_token')
      localStorage.removeItem('username')
      localStorage.removeItem('role')
      store.clearUser()
      mountCountdownBanner(
        'Authentication expired.',
        3,
        3000,
        '/login'
      )
    }
  } catch (err) {
    console.error('Auth check failed:', err)
    localStorage.removeItem('jwt_token')
    localStorage.removeItem('username')
    localStorage.removeItem('role')
    store.clearUser()
    mountCountdownBanner(
      'Authentication error.',
      3,
      3000,
      '/login'
    )
  }
})

export default router

function mountCountdownBanner(baseText, seconds, durationMs, redirectUrl) {
  const mountNode = document.createElement('div')
  document.body.appendChild(mountNode)

  createApp({
    setup() {
      const remaining = ref(seconds)
      onMounted(() => {
        const timer = setInterval(() => {
          remaining.value--
          if (remaining.value <= 0) {
            clearInterval(timer)
            window.location.href = redirectUrl
          }
        }, 1000)
      })
      return () => h(RedirectBanner, {
        message: `${baseText}\nRedirecting in ${remaining.value}…`,
        duration: durationMs,
        redirectUrl
      })
    }
  }).mount(mountNode)
}
