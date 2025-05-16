import {createRouter, createWebHistory} from 'vue-router'
import Login from '@/pages/Login.vue'
import Home from '@/pages/Home.vue'
import WaitingRoom from '@/pages/WaitingRoom.vue'
import DoctorPanel from "@/pages/DoctorPanel.vue";
import RegistryPanel from "@/pages/RegistryPanel.vue";

const routes = [
  {path: '/', component: Home},
  {path: '/login', component: Login},
  {path: '/waiting-room', component: WaitingRoom},
  {path: '/doctor', component: DoctorPanel},
  {path: '/registry', component: RegistryPanel}
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
