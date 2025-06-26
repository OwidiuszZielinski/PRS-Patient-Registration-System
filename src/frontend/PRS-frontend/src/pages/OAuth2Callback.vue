<template>
  <div class="oauth-callback-page">
    <div class="callback-container">
      <div class="callback-card">
        <div v-if="isLoading" class="loading-section">
          <v-progress-circular
            indeterminate
            color="#764abc"
            size="64"
          ></v-progress-circular>
          <h2 class="loading-text">Processing login...</h2>
          <p class="loading-subtext">Please wait while we complete your authentication.</p>
        </div>
        
        <div v-else-if="error" class="error-section">
          <v-icon size="64" color="#f44336" class="mb-4">mdi-alert-circle</v-icon>
          <h2 class="error-text">Authentication Failed</h2>
          <p class="error-message">{{ error }}</p>
          <v-btn 
            color="#764abc" 
            @click="$router.push('/login')"
            class="mt-4"
          >
            Back to Login
          </v-btn>
        </div>
        
        <div v-else class="success-section">
          <v-icon size="64" color="#4caf50" class="mb-4">mdi-check-circle</v-icon>
          <h2 class="success-text">Login Successful!</h2>
          <p class="success-message">Welcome, {{ username }}!</p>
          <v-btn 
            color="#764abc" 
            @click="redirectToHome"
            class="mt-4"
          >
            Continue to Application
          </v-btn>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { store } from '@/store.js'

export default {
  name: 'OAuth2Callback',
  data() {
    return {
      isLoading: true,
      error: null,
      username: null
    }
  },
  async mounted() {
    try {
      console.log('OAuth2Callback mounted')
      
      // Check if we have OAuth2 response data in URL or localStorage
      const urlParams = new URLSearchParams(window.location.search)
      const token = urlParams.get('token')
      const username = urlParams.get('username')
      const email = urlParams.get('email')
      const role = urlParams.get('role')
      
      console.log('URL params:', { token: !!token, username, email, role })
      
      if (token && username) {
        console.log('Storing user data in localStorage')
        
        // Store user data
        localStorage.setItem('jwt_token', token)
        localStorage.setItem('username', username)
        localStorage.setItem('email', email)
        localStorage.setItem('role', role)
        
        console.log('Updating store')
        
        // Update store
        store.updateUser({
          token: token,
          username: username,
          email: email,
          role: role
        })
        
        console.log('Store updated:', store.user)
        
        this.username = username
        this.isLoading = false
        
        console.log('Redirecting in 2 seconds...')
        
        // Redirect after a short delay
        setTimeout(() => {
          this.redirectToHome()
        }, 2000)
      } else {
        console.log('No token or username in URL, checking localStorage')
        
        // Try to get data from localStorage (if redirected from backend)
        const storedToken = localStorage.getItem('jwt_token')
        const storedUsername = localStorage.getItem('username')
        
        console.log('Stored data:', { token: !!storedToken, username: storedUsername })
        
        if (storedToken && storedUsername) {
          this.username = storedUsername
          this.isLoading = false
          
          console.log('Using stored data, redirecting...')
          
          setTimeout(() => {
            this.redirectToHome()
          }, 2000)
        } else {
          console.log('No stored data found')
          this.error = 'No authentication data received. Please try logging in again.'
          this.isLoading = false
        }
      }
    } catch (error) {
      console.error('OAuth2 callback error:', error)
      this.error = 'Authentication failed. Please try again.'
      this.isLoading = false
    }
  },
  methods: {
    redirectToHome() {
      // Redirect based on user role
      const role = store.user.role
      
      console.log('redirectToHome called with role:', role)
      console.log('Current store state:', store.user)
      
      let targetPath = '/'
      
      if (role === 'ADMIN') {
        targetPath = '/registry'
      } else if (role === 'PATIENT') {
        targetPath = '/'
      } else if (role === 'DOCTOR') {
        targetPath = '/doctor'
      } else if (role === 'WAITING_ROOM') {
        targetPath = '/waiting-room'
      } else {
        targetPath = '/'
      }
      
      console.log('Redirecting to:', targetPath)
      
      this.$router.push(targetPath)
    }
  }
}
</script>

<style scoped>
.oauth-callback-page {
  background-color: #1e1f22;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.callback-container {
  width: 100%;
  max-width: 500px;
}

.callback-card {
  background-color: #212121;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
  width: 100%;
}

.loading-section,
.error-section,
.success-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.loading-text,
.error-text,
.success-text {
  font-size: 1.5rem;
  font-weight: 700;
  color: #ffffff;
  margin: 20px 0 10px 0;
}

.loading-subtext,
.error-message,
.success-message {
  font-size: 1rem;
  color: #cccccc;
  margin-bottom: 20px;
  text-align: center;
}

.error-message {
  color: #ffcdd2;
}

.success-message {
  color: #c8e6c9;
}
</style> 