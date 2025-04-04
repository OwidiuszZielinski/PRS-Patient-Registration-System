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
      <v-btn rounded="lg" text to="/" class="custom-btn">Home</v-btn>
      <v-btn rounded="lg" text to="/login" class="custom-btn">Login</v-btn>
      <v-btn rounded="lg" text to="/waiting-room" class="custom-btn">Waiting Room</v-btn>
    </v-app-bar>

    <v-main>
      <router-view @toggle-fullscreen="handleFullscreenChange"></router-view>
    </v-main>
  </v-app>
</template>

<script>
export default {
  name: 'App',
  data() {
    return {
      fullscreen: false
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
  color: #764ABC; /* Ten sam fiolet co ikona */
  font-size: 1.5rem;
  letter-spacing: 1px;
}
.no-hover::before {
  display: none;
}
.v-btn--active.no-hover::before {
  display: none !important;
}

.custom-btn {
  color: #764ABC !important;
  font-weight: 500;
  letter-spacing: 0.5px;
  margin: 0 4px;
}

.v-app-bar {
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  padding: 0 16px;
}
</style>
