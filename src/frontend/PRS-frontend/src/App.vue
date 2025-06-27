<template>
  <v-app>
    <v-app-bar v-if="!fullscreen">
      <router-link
        to="/"
        class="d-flex align-center ml-4 text-decoration-none"
        style="height: 100%;"
      >
        <v-icon size="28" color="#764ABC" class="mr-2">mdi-heart-pulse</v-icon>
        <span class="custom-title font-weight-bold" style="color: #764ABC;">PRS</span>
      </router-link>
      <v-spacer></v-spacer>
      <template v-if="isLoggedIn">
        <!-- ADMIN - wszystkie przyciski -->
        <template v-if="userRole === 'ADMIN'">
          <v-btn rounded="lg" text to="/waiting-room" class="custom-btn">Waiting Room</v-btn>
          <v-btn rounded="lg" text to="/registry" class="custom-btn">Registration</v-btn>
          <v-btn rounded="lg" text to="/doctor" class="custom-btn">Doctor's office</v-btn>
          <v-btn rounded="lg" text to="/patient-view" class="custom-btn">My account</v-btn>
          <v-btn rounded="lg" text to="/logout" class="custom-btn">Logout</v-btn>
        </template>
        <!-- PATIENT - My Account i Logout -->
        <template v-else-if="userRole === 'PATIENT'">
          <v-btn rounded="lg" text to="/patient-view" class="custom-btn">My Account</v-btn>
          <v-btn rounded="lg" text to="/logout" class="custom-btn">Logout</v-btn>
        </template>
        <!-- WAITING_ROOM - tylko Waiting Room -->
        <template v-else-if="userRole === 'WAITING_ROOM'">
          <v-btn rounded="lg" text to="/waiting-room" class="custom-btn">Waiting Room</v-btn>
        </template>
        <!-- DOCTOR - Waiting Room, Doctor's office i Logout -->
        <template v-else-if="userRole === 'DOCTOR'">
          <v-btn rounded="lg" text to="/waiting-room" class="custom-btn">Waiting Room</v-btn>
          <v-btn rounded="lg" text to="/doctor" class="custom-btn">Doctor's office</v-btn>
          <v-btn rounded="lg" text to="/logout" class="custom-btn">Logout</v-btn>
        </template>
      </template>
    </v-app-bar>

    <v-main>
      <router-view @toggle-fullscreen="handleFullscreenChange"></router-view>
    </v-main>
  </v-app>
</template>

<script>
import { store } from '@/store.js'

export default {
  name: 'App',
  data() {
    return {
      fullscreen: false
    }
  },
  computed: {
    userRole() {
      return store.user.role;
    },
    isLoggedIn() {
      return !!store.user.token;
    }
  },
  methods: {
    handleFullscreenChange(isFullscreen) {
      this.fullscreen = isFullscreen;
    }
  },
  mounted() {
    document.addEventListener('fullscreenchange', () => {
      this.fullscreen = !!document.fullscreenElement;
    });
  },
  beforeUnmount() {
    document.removeEventListener('fullscreenchange', () => {
      this.fullscreen = !!document.fullscreenElement;
    });
  }
}
</script>

<style scoped>
.custom-title {
  color: #764ABC;
  font-size: 1.5rem;
  letter-spacing: 1px;
}

.custom-btn {
  color: #764ABC !important;
  font-weight: 500;
  letter-spacing: 0.5px;
  margin: 0 4px;
}
</style>
