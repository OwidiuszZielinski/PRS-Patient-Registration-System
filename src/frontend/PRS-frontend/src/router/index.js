import { createRouter, createWebHistory } from 'vue-router'
import { createApp, h, ref, onMounted } from 'vue'
import axios from 'axios'

import Home from '@/pages/Home.vue'
import WaitingRoom from '@/pages/WaitingRoom.vue'
import DoctorPanel from '@/pages/DoctorPanel.vue'
import RegistryPanel from '@/pages/RegistryPanel.vue'
import LogoutPanel from '@/pages/LogoutPanel.vue'
import RedirectBanner from '@/components/RedirectBanner.vue'

const routes = [
  { path: '/', component: Home },
  { path: '/waiting-room', component: WaitingRoom },
  { path: '/doctor', component: DoctorPanel },
  { path: '/registry', component: RegistryPanel },
  { path: '/logout', component: LogoutPanel }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach(async (to, from, next) => {
  try {
    const { data: isAuth } = await axios.get('http://localhost:8080/auth', { withCredentials: true })
    if (!isAuth) {
      mountCountdownBanner(
        'Need authentication.',
        3,
        3000,
        'http://localhost:8080/login'
      )
    } else {
      next()
    }
  } catch (err) {
    console.error('Auth check failed:', err)
    mountCountdownBanner(
      'Authentication error.',
      3,
      3000,
      'http://localhost:8080/login'
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
