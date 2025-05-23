import {createRouter, createWebHistory} from 'vue-router'
import Home from '@/pages/Home.vue'
import WaitingRoom from '@/pages/WaitingRoom.vue'
import DoctorPanel from "@/pages/DoctorPanel.vue";
import RegistryPanel from "@/pages/RegistryPanel.vue";
import LogoutPanel from "@/pages/LogoutPanel.vue";

const routes = [
  {path: '/', component: Home},
  {path: '/waiting-room', component: WaitingRoom},
  {path: '/doctor', component: DoctorPanel},
  {path: '/registry', component: RegistryPanel},
  {path: '/logout', component: LogoutPanel}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
