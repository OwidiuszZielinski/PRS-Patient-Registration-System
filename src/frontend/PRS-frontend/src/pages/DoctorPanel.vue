<template>
  <v-app>
    <v-container class="fill-height">
      <v-row justify="center">
        <v-col cols="12" md="8" lg="6">
          <v-card class="elevation-12 pa-4">
            <v-card-title class="text-center text-h4 font-weight-bold mb-4">
              <v-icon large color="#764abc" class="mr-2">mdi-doctor</v-icon>
              Doctor Panel
            </v-card-title>

            <!-- Informacje o gabinecie -->
            <v-card outlined class="mb-6">
              <v-card-text class="pa-4">
                <v-select
                  v-model="selectedOffice"
                  :items="offices"
                  label="Wybierz gabinet"
                  outlined
                  dense
                ></v-select>
                <div class="text-h6 text-center">Gabinet: {{ selectedOffice }}</div>
              </v-card-text>
            </v-card>

            <!-- Aktualna wizyta -->
            <v-card outlined class="mb-6 current-visit-card" v-if="currentVisit">
              <v-card-text class="pa-4 text-center">
                <div class="text-h6 mb-2">Aktualna wizyta:</div>
                <v-chip x-large color="primary" class="mr-2">
                  <span class="display-1">{{ currentVisit.number }}</span>
                </v-chip>
                <div class="mt-4">
                  <v-btn
                    color="error"
                    large
                    @click="endVisit"
                    :loading="loading"
                  >
                    <v-icon left>mdi-close-circle</v-icon>
                    Zakończ wizytę
                  </v-btn>
                </div>
              </v-card-text>
            </v-card>

            <!-- Kolejka oczekujących -->
            <v-card outlined class="mb-4">
              <v-card-title class="text-h6">Kolejka oczekujących</v-card-title>
              <v-card-text class="pa-4">
                <v-list>
                  <v-list-item
                    v-for="ticket in queue"
                    :key="ticket.number"
                  >
                    <v-list-item-content>
                      <v-list-item-title class="text-h5">
                        {{ ticket.number }}
                      </v-list-item-title>
                      <v-list-item-subtitle>
                        Czas oczekiwania: ~{{ ticket.waitTime }} min
                      </v-list-item-subtitle>
                    </v-list-item-content>
                    <v-list-item-action>
                      <v-btn
                        color="success"
                        @click="startVisit(ticket)"
                        :disabled="!!currentVisit"
                      >
                        <v-icon left>mdi-play-circle</v-icon>
                        Rozpocznij
                      </v-btn>
                    </v-list-item-action>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-card>

            <!-- Statystyki -->
            <v-card outlined>
              <v-card-text class="d-flex justify-space-around text-center">
                <div>
                  <div class="text-h6">Aktualna</div>
                  <div class="text-h4 primary--text">{{ currentVisit ? currentVisit.number : 'Brak' }}</div>
                </div>
                <div>
                  <div class="text-h6">Oczekujących</div>
                  <div class="text-h4 secondary--text">{{ queue.length }}</div>
                </div>
                <div>
                  <div class="text-h6">Dzisiaj</div>
                  <div class="text-h4 accent--text">{{ completedVisits.length }}</div>
                </div>
              </v-card-text>
            </v-card>
          </v-card>
        </v-col>
      </v-row>
    </v-container>

    <!-- Powiadomienie -->
    <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000">
      {{ snackbarText }}
    </v-snackbar>
  </v-app>
</template>

<script setup>
import { ref, computed } from "vue";

// Dane
const selectedOffice = ref("1");
const offices = ref(["1", "2", "3", "4"]);
const loading = ref(false);
const snackbar = ref(false);
const snackbarText = ref("");
const snackbarColor = ref("success");

// Kolejka oczekujących
const queue = ref([
  { number: "A103", waitTime: 5 },
  { number: "B201", waitTime: 15 },
  { number: "C305", waitTime: 25 },
  { number: "A104", waitTime: 35 }
]);

// Aktualna wizyta
const currentVisit = ref(null);

// Zakończone wizyty
const completedVisits = ref([]);

// Rozpocznij wizytę
const startVisit = (ticket) => {
  currentVisit.value = {
    number: ticket.number,
    startTime: new Date()
  };

  // Usuń z kolejki
  queue.value = queue.value.filter(t => t.number !== ticket.number);

  showNotification(`Rozpoczęto wizytę dla numeru ${ticket.number}`, "success");
};

// Zakończ wizytę
const endVisit = () => {
  loading.value = true;

  setTimeout(() => {
    if (currentVisit.value) {
      completedVisits.value.push({
        number: currentVisit.value.number,
        duration: Math.floor((new Date() - currentVisit.value.startTime) / 60000)
      });

      showNotification(`Zakończono wizytę dla numeru ${currentVisit.value.number}`, "success");
      currentVisit.value = null;
    }

    loading.value = false;
  }, 1000);
};

// Pokaz powiadomienie
const showNotification = (text, color) => {
  snackbarText.value = text;
  snackbarColor.value = color;
  snackbar.value = true;
};
</script>

<style scoped>
.current-visit-card {
  border-left: 6px solid var(--v-primary-base);
  background-color: rgba(25, 118, 210, 0.05);
}

.v-list-item {
  border-bottom: 1px solid #eee;
}

.v-list-item:last-child {
  border-bottom: none;
}
</style>
