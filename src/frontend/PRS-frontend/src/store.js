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

  completedVisits: []
})

export const avgWaitTime = computed(() => {
  if (!store.queue.length) return 0
  const sum = store.queue.reduce((a, t) => a + t.waitTime, 0)
  return Math.round(sum / store.queue.length)
})
