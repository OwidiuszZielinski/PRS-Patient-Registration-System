<template>
  <v-container class="home-page" fluid>
    <!-- Full screen medical background animation -->
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

    <!-- Content container -->
    <div class="content-container">
      <!-- Welcome Card -->
      <v-row class="justify-center" no-gutters>
        <v-col cols="12" class="d-flex justify-center mb-6">
          <div class="welcome-container">
            <div class="welcome-card">
              <h1 class="welcome-title">
                <span class="welcome-highlight">Welcome in PRS</span>
              </h1>
              <p class="welcome-subtitle">Modern Patient Registration System</p>
              <div class="welcome-decoration">
                <div class="decoration-line"></div>
                <div class="decoration-circle"></div>
                <div class="decoration-line"></div>
              </div>
            </div>
          </div>
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
                <!-- Left Column -->
                <v-col cols="12" md="6" class="left-column">
                  <!-- Development Team Section -->
                  <div class="creators-section">
                    <h3 class="section-title text-h6 mb-4">
                      <v-icon left>mdi-account-group</v-icon>
                      Development Team
                    </h3>
                    <v-list dense class="creators-list">
                      <v-list-item v-for="(creator, index) in creators" :key="index">
                        <v-list-item-icon>
                          <v-icon color="primary">mdi-laptop</v-icon>
                        </v-list-item-icon>
                        <v-list-item-content>
                          <v-list-item-title class="creator-name">{{ creator }}</v-list-item-title>
                        </v-list-item-content>
                      </v-list-item>
                    </v-list>
                  </div>

                  <!-- Technologies Section -->
                  <div class="technologies-section mt-8">
                    <h3 class="section-title text-h6 mb-4">
                      <v-icon left>mdi-cog</v-icon>
                      Tech Stack
                    </h3>
                    <v-chip-group column>
                      <v-chip
                        v-for="(tech, index) in technologies"
                        :key="index"
                        :href="tech.link"
                        target="_blank"
                        small
                        outlined
                        class="ma-1 tech-chip"
                      >
                        <v-avatar left>
                          <v-img :src="tech.icon" :alt="tech.name" width="20" />
                        </v-avatar>
                        {{ tech.name }}
                      </v-chip>
                    </v-chip-group>
                  </div>
                </v-col>

                <!-- Right Column -->
                <v-col cols="12" md="6" class="right-column">
                  <!-- Resource Links Section -->
                  <div class="resource-links">
                    <h3 class="section-title text-h6 mb-4">
                      <v-icon left>mdi-link</v-icon>
                      Resources
                    </h3>

                    <v-btn
                      href="https://github.com/twoj-projekt"
                      target="_blank"
                      color="black"
                      class="mb-3 resource-btn"
                      block
                      depressed
                    >
                      <v-icon left>mdi-github</v-icon>
                      GitHub Repository
                    </v-btn>

                    <v-btn
                      href="https://github.com/twoj-projekt/blob/main/README.md"
                      target="_blank"
                      color="grey darken-2"
                      class="mb-3 resource-btn"
                      block
                      depressed
                    >
                      <v-icon left>mdi-text-box</v-icon>
                      Project README
                    </v-btn>

                    <v-btn
                      color="primary"
                      class="mb-3 resource-btn"
                      block
                      depressed
                    >
                      <v-icon left>mdi-file-document</v-icon>
                      Documentation
                    </v-btn>
                  </div>

                  <!-- Project Description -->
                  <div class="project-description mt-8">
                    <h3 class="section-title text-h6 mb-2">
                      <v-icon left>mdi-information</v-icon>
                      Project Info
                    </h3>
                    <p class="text-body-2 description-text">
                      This project was developed as part of our coursework,
                      showcasing modern web development technologies and best practices.
                    </p>
                  </div>
                </v-col>
              </v-row>
            </v-card-text>
          </v-card>
        </v-col>
      </v-row>
    </div>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      creators: [
        'Owidiusz Zieliński',
        'Aleksandra Wrzesień',
        'Aleksandra Wójcicka',
        'Michał Zaleśny'
      ],
      technologies: [
        { name: 'PostgreSQL', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg', link: 'https://www.postgresql.org/' },
        { name: 'Vue.js', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuejs/vuejs-original.svg', link: 'https://vuejs.org/' },
        { name: 'Java', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg', link: 'https://java.com/' },
        { name: 'Spring Boot', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg', link: 'https://spring.io/projects/spring-boot' },
        { name: 'Docker', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg', link: 'https://www.docker.com/' },
        { name: 'Vuetify', icon: 'https://cdn.jsdelivr.net/gh/devicons/devicon/icons/vuetify/vuetify-original.svg', link: 'https://vuetifyjs.com/' }
      ],
      medicalIcons: [
        { name: 'mdi-medical-bag', left: 5, bottom: -50, size: 60, delay: 0, duration: 18, opacity: 0.4 },
        { name: 'mdi-heart-pulse', left: 15, bottom: -80, size: 70, delay: 2, duration: 20, opacity: 0.5 },
        { name: 'mdi-hospital-box', left: 25, bottom: -40, size: 50, delay: 1, duration: 22, opacity: 0.3 },
        { name: 'mdi-pill', left: 35, bottom: -70, size: 45, delay: 3, duration: 17, opacity: 0.4 },
        { name: 'mdi-stethoscope', left: 45, bottom: -30, size: 65, delay: 0.5, duration: 19, opacity: 0.6 },
        { name: 'mdi-needle', left: 55, bottom: -60, size: 55, delay: 2.5, duration: 21, opacity: 0.3 },
        { name: 'mdi-clipboard-pulse', left: 65, bottom: -40, size: 60, delay: 1.5, duration: 18, opacity: 0.5 },
        { name: 'mdi-doctor', left: 75, bottom: -80, size: 70, delay: 0.8, duration: 23, opacity: 0.4 },
        { name: 'mdi-ambulance', left: 85, bottom: -50, size: 65, delay: 3.2, duration: 16, opacity: 0.5 },
        { name: 'mdi-medical-cotton-swab', left: 95, bottom: -30, size: 40, delay: 1.8, duration: 20, opacity: 0.3 },
        { name: 'mdi-hospital', left: 10, bottom: -120, size: 80, delay: 0.2, duration: 25, opacity: 0.4 },
        { name: 'mdi-medication', left: 30, bottom: -90, size: 55, delay: 1.2, duration: 19, opacity: 0.5 },
        { name: 'mdi-heart-plus', left: 50, bottom: -150, size: 75, delay: 0.7, duration: 22, opacity: 0.3 },
        { name: 'mdi-bacteria', left: 70, bottom: -110, size: 65, delay: 2.3, duration: 18, opacity: 0.4 },
        { name: 'mdi-hospital-marker', left: 90, bottom: -140, size: 70, delay: 1.5, duration: 20, opacity: 0.5 }
      ]
    }
  }
}
</script>

<style scoped>
.home-page {
  background-color: #121212;
  min-height: 100vh;
  padding: 40px 20px;
  overflow-x: hidden;
  position: relative;
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

.content-container {
  position: relative;
  z-index: 1;
}

.medical-icon {
  position: absolute;
  color: rgba(118, 74, 188, 0.4);
  animation: floatMedicalIcon 15s infinite linear;
  z-index: 0;
  filter: drop-shadow(0 0 5px rgba(118, 74, 188, 0.3));
  transition: all 0.3s ease;
  will-change: transform;
}

.medical-icon:hover {
  color: rgba(118, 74, 188, 0.8);
  filter: drop-shadow(0 0 15px rgba(118, 74, 188, 0.6));
  transform: scale(1.5) !important;
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

/* Welcome section styles */
.welcome-container {
  width: 100%;
  max-width: 1200px;
  background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%);
  border-radius: 24px;
  padding: 4rem;
  border: 1px solid rgba(118, 74, 188, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  margin: 20px 0;
  animation: fadeIn 1s ease-out;
  position: relative;
  z-index: 1;
}

.welcome-card {
  text-align: center;
  max-width: 600px;
  margin: 0 auto;
}

.welcome-title {
  font-size: 2.8rem;
  color: rgb(118, 74, 188);
  margin-bottom: 0.5rem;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
  animation: slideInDown 0.8s ease-out;
}

.welcome-subtitle {
  font-size: 1.6rem;
  color: rgba(118, 74, 188, 0.8);
  font-weight: 300;
  margin-top: 0;
  letter-spacing: 1px;
  animation: slideInUp 0.8s ease-out;
}

.welcome-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2rem 0;
  animation: fadeIn 1.5s ease-out;
}

.decoration-line {
  height: 2px;
  width: 80px;
  background: linear-gradient(90deg, transparent, rgb(118, 74, 188), transparent);
  opacity: 0.6;
}

.decoration-circle {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background-color: rgb(118, 74, 188);
  margin: 0 20px;
  box-shadow: 0 0 10px rgba(118, 74, 188, 0.5);
  animation: pulse 2s infinite;
}

/* Info card styles */
.info-card {
  background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%);
  border: 1px solid rgba(118, 74, 188, 0.3);
  box-shadow: 0 0 20px rgba(118, 74, 188, 0.2);
  position: relative;
  z-index: 1;
}

.info-title {
  font-size: 2rem;
  color: rgb(118, 74, 188);
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
}

.left-column {
  border-right: 1px solid rgba(118, 74, 188, 0.2);
  padding-right: 32px;
}

.right-column {
  padding-left: 32px;
}

.section-title {
  color: rgba(118, 74, 188, 0.8);
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.creator-name {
  color: rgba(255, 255, 255, 0.8);
  transition: all 0.3s ease;
}

.creator-name:hover {
  color: rgb(118, 74, 188);
  transform: translateX(5px);
}

.technologies-section {
  margin-top: 2rem;
}

.tech-chip {
  transition: all 0.3s ease;
}

.tech-chip:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(118, 74, 188, 0.3);
}

.resource-btn {
  transition: all 0.3s ease;
}

.resource-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 8px rgba(118, 74, 188, 0.3);
}

.project-description {
  margin-top: 2rem;
}

/* Animations */
@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideInDown {
  from {
    transform: translateY(-50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

@keyframes slideInUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

/* Responsive styles */
@media (max-width: 960px) {
  .welcome-container {
    padding: 3rem;
  }

  .welcome-title {
    font-size: 2.5rem;
  }

  .welcome-subtitle {
    font-size: 1.5rem;
  }

  .left-column {
    border-right: none;
    border-bottom: 1px solid rgba(118, 74, 188, 0.2);
    padding-right: 0;
    padding-bottom: 32px;
    margin-bottom: 32px;
  }

  .right-column {
    padding-left: 0;
  }

  .medical-icon {
    font-size: 40px !important;
  }
}

@media (max-width: 600px) {
  .home-page {
    padding: 20px 10px;
  }

  .welcome-container {
    padding: 2rem 1.5rem;
    border-radius: 16px;
  }

  .welcome-title {
    font-size: 2rem;
  }

  .welcome-subtitle {
    font-size: 1.2rem;
  }

  .medical-icon {
    font-size: 30px !important;
  }
}
</style>
