<template>
  <v-container class="home-page" fluid>
    <div class="fullscreen-medical-animation">
      <div
        v-for="(icon, index) in medicalIcons"
        :key="index"
        class="medical-icon"
        :style="{
          left: `${icon.left}%`,
          bottom: `${icon.bottom}px`,
          fontSize: `${icon.size}px`,
          animationDelay: `${icon.delay}s`,
          animationDuration: `${icon.duration}s`,
          opacity: `${icon.opacity}`
        }"
      >
        <v-icon>{{ icon.name }}</v-icon>
      </div>
    </div>

    <!-- Left Side Panel -->
    <div class="side-panel left-panel">
      <v-sheet class="side-sheet pa-3" elevation="4">
        <div class="date-text text-subtitle-1 mb-2">
          {{ todayNumber }} {{ todayMonth }}
        </div>
        <div v-if="weather" class="weather-info d-flex flex-column">
          <div class="d-flex align-center">
            <v-img
              :src="`https://openweathermap.org/img/wn/${weather.icon}@2x.png`"
              width="32"
              height="32"
            />
            <span class="ml-2 text-subtitle-1">{{ weather.temp }}°C</span>
          </div>
          <div class="text-caption">{{ weather.city }}</div>
        </div>
        <div v-else class="text-subtitle-2">Ładowanie pogody...</div>
      </v-sheet>
      <v-row class="justify-center mt-2">
        <v-col cols="12">
          <v-text-field
            v-model="cityInput"
            label="Miasto"
            dense
            hide-details
            clearable
            class="weather-search"
            @keyup.enter="onCitySearch"
          />
        </v-col>
        <v-col cols="12">
          <v-btn
            small
            color="primary"
            class="weather-btn"
            block
            @click="onCitySearch"
          >
            Szukaj
          </v-btn>
        </v-col>
      </v-row>
    </div>

    <!-- Right Side Panel -->
    <div class="side-panel right-panel">
      <v-sheet class="side-sheet pa-3" elevation="11">
        <div class="text-caption">
          Clothing day tip
        </div>

        <div class="d-flex align-center info-text text-subtitle-1 mt-4" style="font-weight: bold;">
          <v-icon class="mr-2" aria-hidden="true">mdi-robot</v-icon>
          AI tip
        </div>

        <div class="info-text text-subtitle-1 mt-4">
          {{ aiResponse || 'Ładuję dane…' }}
        </div>
      </v-sheet>
    </div>


    <!-- Main Content -->
    <div class="content-container">
      <!-- Welcome Card -->
      <v-row class="justify-center" no-gutters>
        <v-col cols="12" md="10" lg="8" class="mt-8">
          <v-card class="welcome-card pa-6" elevation="8" shaped>
            <v-card-title class="welcome-title text-h4 text-center mb-4">
              <span class="welcome-highlight">Welcome in PRS</span>
            </v-card-title>
            <v-card-text class="text-center">
              <p class="welcome-subtitle mb-4">Modern Patient Registration System</p>
              <div class="welcome-decoration d-flex align-center justify-center">
                <div class="decoration-line"></div>
                <div class="decoration-circle"></div>
                <div class="decoration-line"></div>
              </div>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>

      <!-- Info Card -->
      <v-row class="justify-center" no-gutters>
        <v-col cols="12" md="10" lg="8" class="mt-8">
          <v-card class="info-card pa-6" elevation="8" shaped>
            <v-card-title class="info-title text-h4 text-center mb-6">
              About the Project
            </v-card-title>
            <v-card-text class="info-content">
              <v-row>
                <!-- Left: creators + buttons -->
                <v-col cols="12" md="6">
                  <h3 class="section-title text-h6 mb-4 d-flex align-center">
                    <v-icon left color="primary">mdi-account-group</v-icon>
                    Development Team
                  </h3>
                  <v-list dense class="creators-list transparent-creators">
                    <v-list-item v-for="(creator, i) in creators" :key="i" ripple="false">
                      <v-list-item-title class="creator-name d-inline-flex align-center">
                        <v-icon small color="primary" class="mr-2">mdi-laptop</v-icon>
                        {{ creator }}
                      </v-list-item-title>
                    </v-list-item>
                  </v-list>
                  <v-row class="mt-6" justify="start" align="center" dense>
                    <v-col cols="auto">
                      <v-btn outlined rounded elevation="2" class="mx-2"
                             href="https://github.com/OwidiuszZieliński/PRS-Patient-Registration-System"
                             target="_blank">
                        <v-icon left>mdi-github</v-icon>
                        GitHub
                      </v-btn>
                    </v-col>
                    <v-col cols="auto">
                      <v-btn outlined rounded elevation="2" color="grey darken-2" class="mx-2"
                             href="https://github.com/twoj-projekt/blob/main/README.md"
                             target="_blank">
                        <v-icon left>mdi-text-box</v-icon>
                        README
                      </v-btn>
                    </v-col>
                    <v-col cols="auto">
                      <v-btn rounded elevation="2" color="green" class="mx-2"
                             href="http://localhost:8080/swagger-ui/index.html#/"
                             target="_blank">
                        <v-icon left>mdi-file-document</v-icon>
                        Docs
                      </v-btn>
                    </v-col>
                  </v-row>
                </v-col>

                <v-col cols="12" md="6">
                  <div class="project-description pa-6">
                    <!-- Wyeksponowany nagłówek -->
                    <h3 class="text-h6 font-weight-bold mb-4">
                      PRS (Patient Registration System)
                    </h3>
                    <!-- Treść -->
                    <p class="mb-2">
                      A modern application designed to provide end-to-end management of patient
                      registration in healthcare facilities.
                    </p>
                    <p class="mb-2">
                      Thanks to its intuitive user interface and a comprehensive API, you can quickly
                      add new patient records, manage appointment schedules, and generate detailed reports.
                    </p>
                    <p>
                      The system is fully responsive and optimized to handle very large datasets with ease.
                    </p>
                  </div>
                </v-col>

              </v-row>

              <!-- Tech Stack as Carousel -->
              <v-divider class="my-6"/>
              <h3 class="section-title text-h6 mb-4 d-flex align-center justify-center">
                <v-icon left color="primary">mdi-cogs</v-icon>
                Tech Stack
              </h3>
              <v-carousel
                hide-delimiter-background
                show-arrows-on-hover
                height="120"
              >
                <v-carousel-item
                  v-for="(tech, j) in technologies"
                  :key="j"
                >
                  <div class="d-flex flex-column align-center justify-center" style="height:100%">
                    <v-avatar size="48">
                      <v-img :src="tech.icon"/>
                    </v-avatar>
                    <span class="mt-2 tech-name">{{ tech.name }}</span>
                  </div>
                </v-carousel-item>
              </v-carousel>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      temperature: '',
      cityInput: '',
      useManualCity: false,
      weather: null,
      todayNumber: '',
      todayMonth: '',
      aiResponse: '',
      creators: [
        'Owidiusz Zieliński',
        'Aleksandra Wrzesień',
        'Aleksandra Wójcicka',
        'Michał Zaleśny'
      ],
      technologies: [
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg',
          link: 'https://www.postgresql.org/'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg',
          link: 'https://vuejs.org/'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg',
          link: 'https://www.java.com/'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg',
          link: 'https://spring.io/projects/spring-boot'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg',
          link: 'https://www.docker.com/'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuetify/vuetify-original.svg',
          link: 'https://vuetifyjs.com/'
        },
        {
          name: ' ',
          icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/swagger/swagger-original.svg',
          link: 'https://swagger.io/tools/swagger-ui/'
        }
      ],
      medicalIcons: [
        {name: 'mdi-medical-bag', left: 5, bottom: -50, size: 60, delay: 0, duration: 18, opacity: 0.4},
        {name: 'mdi-heart-pulse', left: 15, bottom: -30, size: 40, delay: 2, duration: 20, opacity: 0.3},
        {name: 'mdi-hospital-box', left: 25, bottom: -70, size: 50, delay: 4, duration: 22, opacity: 0.5},
        {name: 'mdi-pill', left: 35, bottom: -40, size: 35, delay: 1, duration: 15, opacity: 0.4},
        {name: 'mdi-needle', left: 45, bottom: -60, size: 45, delay: 3, duration: 19, opacity: 0.3},
        {name: 'mdi-stethoscope', left: 55, bottom: -50, size: 55, delay: 5, duration: 21, opacity: 0.4},
        {name: 'mdi-medical-cotton-swab', left: 65, bottom: -30, size: 30, delay: 2, duration: 17, opacity: 0.3},
        {name: 'mdi-tooth', left: 75, bottom: -70, size: 40, delay: 6, duration: 23, opacity: 0.5},
        {name: 'mdi-eye', left: 85, bottom: -40, size: 45, delay: 3, duration: 20, opacity: 0.4},
        {name: 'mdi-brain', left: 95, bottom: -60, size: 50, delay: 4, duration: 18, opacity: 0.3}
      ]
    };
  },
  mounted() {
    this.setupDate();
    this.fetchWeather();
  },
  methods: {

    setupDate() {
      const d = new Date();
      this.todayNumber = d.getDate();
      const months = ['Styczeń', 'Luty', 'Marzec', 'Kwiecień', 'Maj', 'Czerwiec', 'Lipiec', 'Sierpień', 'Wrzesień', 'Październik', 'Listopad', 'Grudzień'];
      this.todayMonth = months[d.getMonth()];
    },

    async fetchWeather() {
      const key = '5bdbb4c04b3e437e24390b30ac4dc569';
      const fetchByName = async name => {
        try {
          const res = await axios.get('https://api.openweathermap.org/data/2.5/weather', {
            params: {q: name, units: 'metric', lang: 'pl', appid: key}
          });
          this.temperature = Math.round(res.data.main.temp)
          this.weather = {
            temp: Math.round(res.data.main.temp),
            icon: res.data.weather[0].icon,
            city: res.data.name
          };

        } catch (e) {
          console.error(e);
        }
      };
      const fetchCoords = async (lat, lon) => {
        try {
          const res = await axios.get('https://api.openweathermap.org/data/2.5/weather', {
            params: {lat, lon, units: 'metric', lang: 'pl', appid: key}
          });
          this.weather = {
            temp: Math.round(res.data.main.temp),
            icon: res.data.weather[0].icon,
            city: res.data.name
          };
          this.temperature = this.weather.temp.valueOf()
          await this.fetchTip()
        } catch (e) {
          console.error(e);
        }
      };
      if (this.useManualCity) {
        await fetchByName(this.cityInput.trim());
        await this.fetchTip()
      } else if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          pos => fetchCoords(pos.coords.latitude, pos.coords.longitude),
          () => fetchCoords(52.2297, 21.0122)
        );
      } else {
        await fetchCoords(52.2297, 21.0122);
      }
    },

    async fetchTip() {
      try {
        const res = await axios.get('http://localhost:8080/api/ai/' + this.temperature);
        this.aiResponse = res.data;
        console.log(this.aiResponse);
      } catch (err) {
        console.error(err);
        this.aiResponse = 'Błąd przy pobieraniu danych';
      }
    },
    onCitySearch() {
      if (!this.cityInput) return;
      this.useManualCity = true;
      this.fetchWeather();
    }
  }
};
</script>

<style scoped>
.home-page,
.home-page * {
  color: rgb(118, 74, 188) !important;
}

/* white overrides */
.creator-name,
.description-text,
.tech-chip,
.tech-chip .v-chip__content {
  color: #fff !important;
}

.home-page {
  position: relative;
  min-height: 100vh;
  overflow: hidden;
}

.fullscreen-medical-animation {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  overflow: hidden;
}

.medical-icon {
  position: absolute;
  color: rgba(118, 74, 188, 0.4);
  animation: floatMedicalIcon 15s infinite linear;
  filter: drop-shadow(0 0 5px rgba(118, 74, 188, 0.3));
  transition: all 0.3s ease;
  z-index: 0;
}

.medical-icon:hover {
  color: rgba(118, 74, 188, 0.8);
  filter: drop-shadow(0 0 15px rgba(118, 74, 188, 0.6));
  transform: scale(1.5);
  z-index: 1;
}

@keyframes floatMedicalIcon {
  0% {
    transform: translateY(0) rotate(0deg);
    opacity: 0;
  }
  10% {
    opacity: 0.6;
  }
  50% {
    transform: translateY(-300px) rotate(180deg);
    opacity: 0.4;
  }
  100% {
    transform: translateY(-600px) rotate(360deg);
    opacity: 0;
  }
}

.content-container {
  position: relative;
  z-index: 1;
}

.side-panel {
  position: fixed;
  top: 20px;
  z-index: 2;
}

.side-panel.left-panel {
  left: 20px;
}

.side-panel.right-panel {
  right: 20px;
}

.side-sheet {
  background: linear-gradient(135deg, #1e1e1e, #2a2a2a);
  width: 140px;
  border-radius: 8px;
}

.creator-name {
  transition: all 0.3s ease;
  cursor: pointer;
}

.creator-name:hover {
  transform: translateX(10px);
  color: #fff !important;
}

.creators-list .v-list-item {
  opacity: 0;
  animation: fadeInItem 0.5s forwards;
}

.transparent-creators {
  background: transparent !important;
}

.transparent-creators .creators-list {
  background: transparent !important;
}

.transparent-creators .v-list-item {
  background: transparent !important;
}

.creators-list .v-list-item:nth-child(1) {
  animation-delay: 0.2s;
}

.creators-list .v-list-item:nth-child(2) {
  animation-delay: 0.4s;
}

.creators-list .v-list-item:nth-child(3) {
  animation-delay: 0.6s;
}

.creators-list .v-list-item:nth-child(4) {
  animation-delay: 0.8s;
}

@keyframes fadeInItem {
  to {
    opacity: 1;
  }
}

.resource-links {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.weather-search {
  max-width: 200px;
  width: 100%;
}

.weather-search .v-field {
  background: transparent !important;
}

.weather-search .v-field__outline {
  border-color: rgba(118, 74, 188, 0.6) !important;
}

.weather-search .v-field__input,
.weather-search .v-label {
  color: rgba(118, 74, 188, 0.8) !important;
}

.weather-btn {
  max-width: 200px;
  width: 100%;
  background: transparent !important;
  border: 1px solid rgba(118, 74, 188, 0.8) !important;
  color: rgba(118, 74, 188, 0.8) !important;
}

.weather-btn:hover {
  background: rgba(118, 74, 188, 0.2) !important;
  border-color: rgb(118, 74, 188) !important;
  color: rgb(118, 74, 188) !important;
}

.tech-chip,
.tech-chip .v-chip__content {
  color: #fff !important;
}

.tech-stack-group {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.tech-chip {
  border-color: rgba(118, 74, 188, 0.5);
  transition: all 0.3s ease;
}

.tech-chip:hover {
  background-color: rgba(118, 74, 188, 0.1);
  border-color: rgb(118, 74, 188);
  transform: scale(1.05);
}

.welcome-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 16px 0;
}

.decoration-line {
  height: 1px;
  width: 40px;
  background-color: rgba(118, 74, 188, 0.5);
}

.decoration-circle {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: rgb(118, 74, 188);
  margin: 0 10px;
}

.welcome-card,
.info-card {
  background: linear-gradient(135deg, #1e1e1e, #2a2a2a) !important;
  border: 1px solid rgba(118, 74, 188, 0.3) !important;
}

.welcome-title,
.welcome-subtitle,
.info-title,
.section-title {
  color: rgb(118, 74, 188) !important;
}

.weather-search,
.weather-search .v-field,
.weather-search .v-field__control,
.weather-search .v-field__outline,
.weather-search .v-input__slot {
  background-color: transparent !important;
}
</style>
