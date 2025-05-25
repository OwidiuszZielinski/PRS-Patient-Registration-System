<template>
  <v-app>
    <v-container class="fill-height">
      <v-row justify="center">
        <v-col cols="12" md="10" lg="8">
          <v-card class="elevation-12 pa-4">
            <!-- Pełny ekran + wybór gabinetu + przycisk generowania numeru -->
            <v-card-actions class="justify-between pa-0 ma-0">
              <v-btn icon small @click="toggleFullscreen">
                <v-icon>{{ fullscreen ? 'mdi-fullscreen-exit' : 'mdi-fullscreen' }}</v-icon>
              </v-btn>
              <div class="d-flex align-center">
                <v-menu>
                  <template #activator="{ props }">
                    <v-btn v-bind="props" outlined class="mr-2">
                      Office: {{ ticketOffice }}
                      <v-icon right>mdi-menu-down</v-icon>
                    </v-btn>
                  </template>
                  <v-list>
                    <v-list-item
                      v-for="office in offices"
                      :key="office"
                      @click="ticketOffice = office"
                    >
                      <v-list-item-title>Office {{ office }}</v-list-item-title>
                    </v-list-item>
                  </v-list>
                </v-menu>
                <v-btn color="primary" @click="generateTicket">
                  <v-icon left>mdi-ticket</v-icon> Take ticket
                </v-btn>
              </div>
            </v-card-actions>

            <v-card-title class="text-center text-h4 font-weight-bold mb-4">
              <v-icon large color="#764abc" class="mr-2">mdi-clock-outline</v-icon>
              System kolejkowy
            </v-card-title>

            <!-- Aktualnie obsługiwany -->
            <v-card outlined class="mb-6 current-card">
              <v-card-text class="pa-4 text-center">
                <div class="text-h6 mb-2">Aktualnie obsługiwany:</div>
                <v-chip x-large color="primary" class="mr-2">
                  <span class="display-1">{{ currentTicket.number }}</span>
                </v-chip>
                <div class="text-h6 mt-2">Gabinet {{ currentTicket.office }}</div>
              </v-card-text>
            </v-card>

            <!-- Kolejka oczekujących -->
            <v-card outlined>
              <v-card-text class="pa-4">
                <v-row>
                  <v-col
                    v-for="ticket in waitingTickets"
                    :key="ticket.number"
                    cols="12"
                    sm="6"
                    md="4"
                  >
                    <v-card class="ticket-card">
                      <v-card-text class="text-center">
                        <v-chip color="secondary" class="mb-2">
                          <span class="text-h5">{{ ticket.number }}</span>
                        </v-chip>
                        <div class="text-subtitle-1">Gabinet {{ ticket.office }}</div>
                        <div class="text-caption mt-1">~{{ ticket.waitTime }} min</div>
                      </v-card-text>
                    </v-card>
                  </v-col>
                </v-row>
              </v-card-text>
            </v-card>

            <!-- Statystyki -->
            <v-card outlined class="mt-4">
              <v-card-text class="d-flex justify-space-around text-center">
                <div>
                  <div class="text-h6">Aktualny</div>
                  <div class="text-h4 primary--text">{{ currentTicket.number }}</div>
                </div>
                <div>
                  <div class="text-h6">Oczekujących</div>
                  <div class="text-h4 secondary--text">{{ waitingTickets.length }}</div>
                </div>
                <div>
                  <div class="text-h6">Średni czas</div>
                  <div class="text-h4 accent--text">{{ avgWaitTime }} min</div>
                </div>
              </v-card-text>
            </v-card>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>

<script setup>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { store, avgWaitTime } from '@/store'

const fullscreen = ref(false)

const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
      .then(() => { fullscreen.value = true })
      .catch(err => console.error('Fullscreen error:', err))
  } else {
    document.exitFullscreen()
      .then(() => { fullscreen.value = false })
      .catch(err => console.error('Exit fullscreen error:', err))
  }
}

const handleFullscreenChange = () => {
  fullscreen.value = !!document.fullscreenElement
}

onMounted(() => document.addEventListener('fullscreenchange', handleFullscreenChange))
onBeforeUnmount(() => document.removeEventListener('fullscreenchange', handleFullscreenChange))

const offices = store.offices
const waitingTickets = computed(() => store.queue)
const currentTicket = computed(() => store.currentVisit || { number: '-', office: '-' })
const avgWait = avgWaitTime

const ticketOffice = ref(store.offices[0])

function generateTicket() {
  const letters = 'ABCD'
  const num = letters[Math.floor(Math.random() * letters.length)] +
    Math.floor(100 + Math.random() * 900)
  const wait = store.queue.filter(t => t.office === ticketOffice.value).length * 5 + 5
  store.queue.push({ number: num, office: ticketOffice.value, waitTime: wait })
}
</script>

<style scoped>
.current-card {
  border-left: 6px solid var(--v-primary-base);
}
.ticket-card {
  border-left: 4px solid var(--v-secondary-base);
  transition: all 0.3s ease;
}
.ticket-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}
.v-btn {
  transition: all 0.3s ease;
}
</style>
