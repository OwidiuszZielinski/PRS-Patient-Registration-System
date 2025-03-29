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
      <v-btn color="primary" class="mt-4" @click="nextNumber">Następny</v-btn>
    </v-container>
  </v-app>
</template>

<script setup>
import {ref} from 'vue';

const currentNumber = ref(1);
const waitingNumbers = ref([2, 3, 4, 5, 6, 7]);

const nextNumber = () => {
  currentNumber.value++;
  waitingNumbers.value.shift();
  waitingNumbers.value.push(waitingNumbers.value[waitingNumbers.value.length - 1] + 1);
};
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
