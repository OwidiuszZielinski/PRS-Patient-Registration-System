<template>
  <v-container class="home-page" fluid>
    <v-row class="justify-center" no-gutters>
      <!-- Welcome Card -->
      <v-col cols="12" class="d-flex justify-center mb-6 ">
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

      <!-- Info Card -->
      <v-col class="d-flex justify-sm-center mt-16">
        <div class="info-container">
          <div class="info-card">
            <h2 class="info-title">About</h2>
            <div class="info-content">
              <div class="creators-section">
                <h3 class="section-title">Development Team</h3>
                <ul class="creators-list">
                  <li class="creator-item">💻 Owidiusz Zieliński</li>
                  <li class="creator-item">💻 Aleksandra Wrzesień</li>
                  <li class="creator-item">💻 Michał Zaleśny</li>
                </ul>
              </div>

              <div class="decoration-line vertical"></div>

              <div class="actions-section">
                <a href="#" class="doc-link">
                  📚 Documentation
                  <span class="link-underline"></span>
                </a>
                <v-btn
                  to="/docs"
                  color="#764abc"
                  class="cta-button"
                  depressed
                  x-large
                  block
                >
                  🚀 Get Started
                </v-btn>
              </div>
            </div>
          </div>
        </div>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'HomePage'
}
</script>

<style scoped>
<style scoped>
.home-page {
  background-color: #121212;
  min-height: 100vh;
  padding: 40px 20px;
}

.welcome-container,
.info-container {
  width: 100%;
  max-width: 1200px;
  background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%);
  border-radius: 24px;
  padding: 4rem;
  border: 1px solid rgba(118, 74, 188, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  margin: 20px 0;
}

.welcome-title {
  font-size: 3.5rem;
  margin-bottom: 1rem;
}

.welcome-subtitle {
  font-size: 2rem;
}

.info-title {
  font-size: 2.5rem;
  margin-bottom: 3rem;
}

.info-content {
  display: flex;
  justify-content: space-around;
  gap: 3rem;
  padding: 0 2rem;
}

.cta-button {
  margin-top: 2rem;
  font-size: 1.2rem;
  padding: 20px 40px !important;
}

@media (max-width: 960px) {
  .welcome-container,
  .info-container {
    padding: 3rem;
    max-width: 800px;
  }

  .welcome-title {
    font-size: 2.5rem;
  }

  .welcome-subtitle {
    font-size: 1.5rem;
  }

  .info-title {
    font-size: 2rem;
  }
}

@media (max-width: 600px) {
  .home-page {
    padding: 20px 10px;
  }

  .welcome-container,
  .info-container {
    padding: 2rem 1.5rem;
    border-radius: 16px;
  }


  .welcome-subtitle {
    font-size: 1.2rem;
  }

  .info-content {
    flex-direction: column;
    padding: 0;
  }

  .decoration-line.vertical {
    height: 2px;
    width: 100%;
    margin: 1.5rem 0;
  }

  .cta-button {
    font-size: 1rem;
    padding: 16px 32px !important;
  }
}

/* Zachowane oryginalne style dekoracyjne */
.welcome-title {
  font-size: 2.8rem;
  color: rgb(118, 74, 188);
  margin-bottom: 0.5rem;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
}

.welcome-subtitle {
  font-size: 1.6rem;
  color: rgba(118, 74, 188, 0.8);
  font-weight: 300;
  margin-top: 0;
  letter-spacing: 1px;
}

.info-title {
  font-size: 2rem;
  color: rgb(118, 74, 188);
  text-align: center;
  margin-bottom: 2rem;
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
}

.cta-button {
  background: rgba(118, 74, 188, 0.2) !important;
  color: rgb(118, 74, 188) !important;
  border: 5px solid rgba(118, 74, 188, 0.3) !important;
  letter-spacing: 1px;
  transition: all 0.3s ease !important;
}

.cta-button:hover {
  background: rgba(118, 74, 188, 0.3) !important;
  box-shadow: 0 0 15px rgba(118, 74, 188, 0.2);
  transform: translateY(-2px);
}

.welcome-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 300px;
  background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%);
  padding: 3rem;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(118, 74, 188, 0.2);
}

.welcome-card {
  text-align: center;
  max-width: 600px;
}

.welcome-title {
  font-size: 2.8rem;
  color: rgb(118, 74, 188);
  margin-bottom: 0.5rem;
  font-weight: 600;
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
}

.welcome-highlight {
  position: relative;
}

.welcome-subtitle {
  font-size: 1.6rem;
  color: rgba(118, 74, 188, 0.8);
  font-weight: 300;
  margin-top: 0;
  letter-spacing: 1px;
}

.welcome-decoration {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 2rem 0;
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

.welcome-highlight::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(90deg, transparent, rgb(118, 74, 188), transparent);
  transform: scaleX(0);
  transform-origin: right;
  transition: transform 0.6s ease;
}

.welcome-card:hover .welcome-highlight::after {
  transform: scaleX(1);
  transform-origin: left;
}

.info-container {
  background: linear-gradient(135deg, #1e1e1e 0%, #2a2a2a 100%);
  border-radius: 16px;
  padding: 2rem;
  border: 1px solid rgba(118, 74, 188, 0.2);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
}

.info-card {
  color: rgba(255, 255, 255, 0.9);
}

.info-title {
  font-size: 2rem;
  color: rgb(118, 74, 188);
  text-align: center;
  margin-bottom: 2rem;
  text-shadow: 0 0 10px rgba(118, 74, 188, 0.3);
}

.info-content {
  display: flex;
  justify-content: space-between;
  gap: 2rem;
}

.creators-section {
  flex: 1;
}

.section-title {
  color: rgba(118, 74, 188, 0.8);
  font-size: 1.2rem;
  margin-bottom: 1rem;
}

.creators-list {
  list-style: none;
  padding: 0;
}

.creator-item {
  padding: 0.8rem 0;
  color: rgba(255, 255, 255, 0.8);
  border-bottom: 1px solid rgba(118, 74, 188, 0.1);
  transition: transform 0.3s ease;
}

.creator-item:hover {
  transform: translateX(10px);
  color: rgb(118, 74, 188);
}

.decoration-line.vertical {
  width: 2px;
  height: 200px;
  background: linear-gradient(180deg, transparent, rgb(118, 74, 188), transparent);
  opacity: 0.6;
  margin: 0 1.5rem;
}

.actions-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  align-items: center;
}

.doc-link {
  color: rgba(118, 74, 188, 0.8);
  text-decoration: none;
  position: relative;
  padding: 0.5rem 0;
  transition: color 0.3s ease;
}

.link-underline {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 0;
  height: 1px;
  background: rgb(118, 74, 188);
  transition: width 0.3s ease;
}

.doc-link:hover .link-underline {
  width: 100%;
}

.cta-button {
  background: rgba(118, 74, 188, 0.2);
  color: rgb(118, 74, 188);
  padding: 12px 30px;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 1px solid rgba(118, 74, 188, 0.3);
}

.cta-button:hover {
  background: rgba(118, 74, 188, 0.3);
  box-shadow: 0 0 15px rgba(118, 74, 188, 0.2);
  transform: translateY(-2px);
}

@media (max-width: 768px) {
  .info-content {
    flex-direction: column;
  }

  .decoration-line.vertical {
    width: 30%;
    height: 2px;
    margin: 1.5rem 0;
  }
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.2); opacity: 0.7; }
  100% { transform: scale(1); opacity: 1; }
}
</style>
