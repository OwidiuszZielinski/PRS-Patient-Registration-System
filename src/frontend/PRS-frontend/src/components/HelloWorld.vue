<template>
  <v-app>
    <v-container class="fill-height d-flex flex-column justify-center align-center">
      <v-card class="elevation-12 waiting-card">
        <v-card-title class="text-center text-h4 font-weight-bold">
          Aktualny numer
        </v-card-title>
        <v-card-text class="d-flex flex-column align-center">
          <v-chip size="x-large" class="waiting-number">{{ currentNumber }}</v-chip>
          <v-divider class="my-4"></v-divider>
          <div class="text-h6">Oczekujący:</div>
          <v-chip-group column>
            <v-chip v-for="num in waitingNumbers" :key="num" class="waiting-chip">{{ num }}</v-chip>
          </v-chip-group>
        </v-card-text>
      </v-card>

      <!-- Wyświetlanie danych z Fibonacci -->
      <div v-if="fiboData">
        <h2>Fibonacci Data</h2>
        <p>{{ fiboData }}</p>
      </div>

      <v-btn color="primary" class="mt-4" @click="nextNumber">Następny</v-btn>
    </v-container>
  </v-app>
</template>

<script setup>
import { ref, onMounted } from "vue";
import WaitingRoomService from "@/services/WaitingRoomService.js";

// Stan dla aktualnego numeru i oczekujących numerów
const currentNumber = ref(1);
const waitingNumbers = ref([2, 3, 4, 5, 6, 7]);

// Stan dla danych Fibonacci
const fiboData = ref(null);

// Funkcja do przejścia do następnego numeru
const nextNumber = () => {
  currentNumber.value++;
  waitingNumbers.value.shift();
  waitingNumbers.value.push(waitingNumbers.value[waitingNumbers.value.length - 1] + 1);
};

// Pobranie danych po zamontowaniu komponentu
onMounted(async () => {
  try {
    const response = await WaitingRoomService.getFibo(15);
    fiboData.value = response.data;
  } catch (error) {
    console.error("Błąd podczas pobierania danych:", error);
  }
});
</script>

<style scoped>
.waiting-card {
  padding: 20px;
  width: 600px;
  text-align: center;
}

.waiting-number {
  font-size: 48px;
  font-weight: bold;
  padding: 20px;
  background-color: #ff5252;
  color: white;
}

.waiting-chip {
  font-size: 20px;
  padding: 10px;
  background-color: #2196F3;
  color: white;
}
</style>
