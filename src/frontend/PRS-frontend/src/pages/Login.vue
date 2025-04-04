<template>
  <v-app>
    <v-container class="fill-height d-flex flex-column justify-center align-center">
      <v-card class="elevation-12 login-card">
        <v-card-title class="text-center text-h4 font-weight-bold mb-6">
          Logowanie
        </v-card-title>

        <v-card-text>
          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="email"
              label="Email"
              prepend-inner-icon="mdi-email"
              type="email"
              required
              outlined
              rounded
              class="mb-4"
            ></v-text-field>

            <v-text-field
              v-model="password"
              label="Hasło"
              prepend-inner-icon="mdi-lock"
              :type="showPassword ? 'text' : 'password'"
              :append-inner-icon="showPassword ? 'mdi-eye-off' : 'mdi-eye'"
              @click:append-inner="showPassword = !showPassword"
              required
              outlined
              rounded
              class="mb-2"
            ></v-text-field>

            <div class="d-flex justify-space-between align-center mb-6">
              <v-checkbox
                v-model="rememberMe"
                label="Zapamiętaj mnie"
                hide-details
              ></v-checkbox>

              <router-link
                to="/forgot-password"
                class="text-caption text-decoration-none"
              >
                Zapomniałeś hasła?
              </router-link>
            </div>

            <v-btn
              type="submit"
              color="#764abc"
              block
              x-large
              rounded
              class="login-btn"
            >
              Zaloguj się
            </v-btn>
          </v-form>
        </v-card-text>

        <v-card-actions class="justify-center">
          <span class="text-body-2 mr-2">Nie masz konta?</span>
          <router-link
            to="/register"
            class="text-body-2 font-weight-bold text-decoration-none"
          >
            Zarejestruj się
          </router-link>
        </v-card-actions>
      </v-card>
    </v-container>
  </v-app>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// Dane formularza
const email = ref("");
const password = ref("");
const rememberMe = ref(false);
const showPassword = ref(false);

// Obsługa logowania
const handleLogin = async () => {
  if (email.value === "lekarz" && password.value === "lekarz") {
    localStorage.setItem('doctorLoggedIn', 'true');
    await router.push("/doctor");
  }
  if (email.value === "rejestratorka" && password.value === "rejestratorka") {
    localStorage.setItem('registerLoggedIn', 'true');
    await router.push("/registry");
  }
  else {
    alert("Nieprawidłowy login lub hasło!");
  }
};
</script>

<style scoped>
.login-card {
  padding: 40px;
  width: 500px;
  max-width: 100%;
}

.login-btn {
  font-size: 16px;
  font-weight: bold;
  letter-spacing: 1px;
  height: 50px;
  text-transform: uppercase;
}

.v-text-field--outlined >>> fieldset {
  border-color: #764abc;
}

.v-text-field--outlined:hover >>> fieldset {
  border-color: #764abc!important;
}

.v-input--checkbox >>> .v-label {
  color: rgba(0, 0, 0, 0.8);
}

a {
  color: #764abc !important;
  transition: all 0.3s ease;
}

a:hover {
  opacity: 0.8;
}
</style>
