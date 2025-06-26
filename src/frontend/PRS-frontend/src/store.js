// src/store.js
import { reactive, computed } from 'vue'

export const store = reactive({
  offices: ['1','2','3','4'],
  selectedOffice: '1',

  queue: [
    { number: 'A103', office: '1', waitTime: 5 },
    { number: 'B201', office: '2', waitTime: 15 },
    { number: 'C305', office: '3', waitTime: 25 },
    { number: 'A104', office: '1', waitTime: 35 }
  ],

  currentVisit: null,

  completedVisits: [],

  // User management
  user: {
    role: localStorage.getItem('role') || null,
    username: localStorage.getItem('username') || null,
    email: localStorage.getItem('email') || null,
    token: localStorage.getItem('jwt_token') || null
  },

  // Method to update user data
  updateUser(userData) {
    this.user = { ...this.user, ...userData }
  },

  // Method to clear user data (logout)
  clearUser() {
    this.user = {
      role: null,
      username: null,
      email: null,
      token: null
    }
  },

  // Method to refresh user data from localStorage
  refreshUserFromStorage() {
    this.user = {
      role: localStorage.getItem('role'),
      username: localStorage.getItem('username'),
      email: localStorage.getItem('email'),
      token: localStorage.getItem('jwt_token')
    }
  }
})

export const avgWaitTime = computed(() => {
  if (!store.queue.length) return 0
  const sum = store.queue.reduce((a, t) => a + t.waitTime, 0)
  return Math.round(sum / store.queue.length)
})

// Initialize user data on store creation
store.refreshUserFromStorage()
