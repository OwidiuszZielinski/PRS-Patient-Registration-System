<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <h1 class="login-title">Sign In</h1>

        <!-- komunikat o błędzie -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <form @submit.prevent="handleLogin" class="login-form">
          <div class="input-field">
            <i class="mdi mdi-account input-icon"></i>
            <input 
              type="text" 
              v-model="username" 
              required 
              placeholder="Username"
              :disabled="isLoading"
            >
          </div>

          <div class="input-field">
            <i class="mdi mdi-lock input-icon"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              v-model="password" 
              required 
              placeholder="Password"
              :disabled="isLoading"
            >
            <i 
              :class="showPassword ? 'mdi mdi-eye password-toggle' : 'mdi mdi-eye-off password-toggle'"
              @click="togglePassword"
            ></i>
          </div>

          <div class="login-options">
            <label class="remember-me">
              <input type="checkbox" v-model="rememberMe">
              <span>Remember me</span>
            </label>
            <a href="#" class="forgot-password">Forgot password?</a>
          </div>

          <button type="submit" class="login-button" :disabled="isLoading">
            {{ isLoading ? 'Signing In...' : 'Sign In' }}
          </button>
        </form>

        <div class="oauth-section">
          <div class="oauth-divider">
            <span>or sign in with</span>
          </div>
          <div class="oauth-buttons">
            <button @click="handleGoogleOAuth" class="oauth-button google-button" :disabled="isOAuthLoading">
              <svg class="oauth-icon" viewBox="0 0 24 24">
                <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
              </svg>
              {{ isOAuthLoading ? 'Connecting...' : 'Continue with Google' }}
            </button>
            <button @click="handleGitHubOAuth" class="oauth-button github-button" :disabled="isOAuthLoading">
              <svg class="oauth-icon" viewBox="0 0 24 24">
                <path fill="currentColor" d="M12 0c-6.626 0-12 5.373-12 12 0 5.302 3.438 9.8 8.207 11.387.599.111.793-.261.793-.577v-2.234c-3.338.726-4.033-1.416-4.033-1.416-.546-1.387-1.333-1.756-1.333-1.756-1.089-.745.083-.729.083-.729 1.205.084 1.839 1.237 1.839 1.237 1.07 1.834 2.807 1.304 3.492.997.107-.775.418-1.305.762-1.604-2.665-.305-5.467-1.334-5.467-5.931 0-1.311.469-2.381 1.236-3.221-.124-.303-.535-1.524.117-3.176 0 0 1.008-.322 3.301 1.23.957-.266 1.983-.399 3.003-.404 1.02.005 2.047.138 3.006.404 2.291-1.552 3.297-1.23 3.297-1.23.653 1.653.242 2.874.118 3.176.77.84 1.235 1.911 1.235 3.221 0 4.609-2.807 5.624-5.479 5.921.43.372.823 1.102.823 2.222v3.293c0 .319.192.694.801.576 4.765-1.589 8.199-6.086 8.199-11.386 0-6.627-5.373-12-12-12z"/>
              </svg>
              {{ isOAuthLoading ? 'Connecting...' : 'Continue with GitHub' }}
            </button>
          </div>
        </div>

        <div class="register-link">
          <span>No account?</span>
          <a @click="$router.push('/register')" style="cursor: pointer;">Sign up</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'
import { store } from '@/store.js'

export default {
  name: 'Login',
  data() {
    return {
      username: '',
      password: '',
      rememberMe: false,
      showPassword: false,
      isLoading: false,
      errorMessage: '',
      isOAuthLoading: false
    }
  },
  methods: {
    async handleLogin() {
      this.isLoading = true
      this.errorMessage = ''
      
      try {
        // Wysyłanie żądania logowania do backendu
        const response = await axios.post('http://localhost:8080/api/v1/auth/login', {
          username: this.username,
          password: this.password
        }, {
          headers: {
            'Content-Type': 'application/json'
          }
        })

        // Zapisanie tokenu JWT
        const { token, username, email, role } = response.data
        
        // Zapisanie tokenu w localStorage
        localStorage.setItem('jwt_token', token)
        localStorage.setItem('username', username)
        localStorage.setItem('email', email)
        localStorage.setItem('role', role)
        
        // Aktualizacja store
        store.updateUser({ token, username, email, role })
        
        // Ustawienie domyślnego nagłówka Authorization dla wszystkich przyszłych żądań
        axios.defaults.headers.common['Authorization'] = `Bearer ${token}`

        // Przekierowanie na stronę główną po udanym logowaniu
        this.$router.push('/')
      } catch (error) {
        console.error('Login error:', error)
        if (error.response && error.response.status === 401) {
          this.errorMessage = 'Username or password incorrect'
        } else if (error.response && error.response.status === 400) {
          this.errorMessage = 'Invalid credentials'
        } else {
          this.errorMessage = 'Login failed. Please try again.'
        }
      } finally {
        this.isLoading = false
      }
    },
    togglePassword() {
      this.showPassword = !this.showPassword
    },
    async handleGoogleOAuth() {
      this.isOAuthLoading = true
      try {
        window.location.href = 'http://localhost:8080/oauth2/authorization/google'
      } catch (error) {
        this.errorMessage = 'Google OAuth2 connection failed. Please try again later.'
      } finally {
        this.isOAuthLoading = false
      }
    },
    async handleGitHubOAuth() {
      this.isOAuthLoading = true
      try {
        window.location.href = 'http://localhost:8080/oauth2/authorization/github'
      } catch (error) {
        this.errorMessage = 'GitHub OAuth2 connection failed. Please try again later.'
      } finally {
        this.isOAuthLoading = false
      }
    }
  },
  async mounted() {
    // Sprawdzenie czy użytkownik ma już token JWT
    const token = localStorage.getItem('jwt_token')
    if (token) {
      try {
        const response = await axios.get('http://localhost:8080/api/v1/auth/validate', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        })
        if (response.data) {
          // Aktualizacja store z danymi z localStorage
          store.refreshUserFromStorage()
          this.$router.push('/')
        }
      } catch (error) {
        // Token jest nieprawidłowy, usuń go
        localStorage.removeItem('jwt_token')
        localStorage.removeItem('username')
        localStorage.removeItem('email')
        localStorage.removeItem('role')
        store.clearUser()
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  background-color: #1e1f22;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.login-container {
  width: 100%;
  max-width: 500px;
}

.login-card {
  background-color: #212121;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.login-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 30px;
}

.login-form {
  margin-bottom: 20px;
}

.input-field {
  position: relative;
  margin-bottom: 20px;
}

.input-field input {
  width: 100%;
  height: 56px;
  padding: 0 15px 0 45px;
  border: 1px solid rgba(118, 74, 188, 0.5);
  border-radius: 0px;
  font-size: 1rem;
  outline: none;
  transition: all 0.3s;
  background-color: #2a2a2a;
  color: #ffffff;
}

.input-field input:focus {
  border-color: #777;
  box-shadow: 0 0 0 2px rgba(118, 74, 188, 0.2);
}

.input-field input:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.input-icon {
  position: absolute;
  left: 15px;
  top: 18px;
  color: #777;
  font-size: 1.2rem;
}

.password-toggle {
  position: absolute;
  right: 15px;
  top: 18px;
  color: #777;
  cursor: pointer;
  font-size: 1.2rem;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 15px 0 25px;
  font-size: 0.875rem;
}

.remember-me {
  display: flex;
  align-items: center;
  color: #ffffff;
  cursor: pointer;
}

.remember-me input {
  margin-right: 8px;
}

.forgot-password {
  color: #764abc;
  text-decoration: none;
  font-weight: 500;
}

.forgot-password:hover {
  text-decoration: underline;
}

.login-button {
  width: 100%;
  height: 50px;
  background-color: #764abc;
  color: white;
  border: none;
  border-radius: 0px;
  font-size: 1rem;
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 1px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.login-button:hover:not(:disabled) {
  background-color: #5d3997;
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.register-link {
  text-align: center;
  font-size: 0.875rem;
  color: #ffffff;
  margin-top: 20px;
}

.register-link a {
  color: #764abc;
  font-weight: 700;
  text-decoration: none;
  margin-left: 5px;
}

.register-link a:hover {
  text-decoration: underline;
}

.error-message {
  background: #fdecea;
  color: #b71c1c;
  border: 1px solid #f5c6cb;
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-weight: 500;
  text-align: center;
}

.oauth-section {
  margin-top: 30px;
  margin-bottom: 20px;
  text-align: center;
}

.oauth-divider {
  position: relative;
  margin: 20px 0;
  text-align: center;
}

.oauth-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background: rgba(255, 255, 255, 0.3);
}

.oauth-divider span {
  background-color: #212121;
  padding: 0 15px;
  color: #ffffff;
  font-size: 0.875rem;
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.oauth-buttons {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.oauth-button {
  width: 100%;
  height: 48px;
  border: 2px solid #ffffff;
  border-radius: 6px;
  font-size: 0.9rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  background: transparent;
  color: #ffffff;
  position: relative;
  overflow: hidden;
}

.oauth-button:hover:not(:disabled) {
  background-color: #ffffff;
  color: #212121;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 255, 255, 0.2);
}

.oauth-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.oauth-button.google-button:hover:not(:disabled) {
  border-color: #4285F4;
  background-color: #4285F4;
  color: #ffffff;
}

.oauth-button.github-button:hover:not(:disabled) {
  border-color: #333333;
  background-color: #333333;
  color: #ffffff;
}

.oauth-icon {
  width: 20px;
  height: 20px;
  margin-right: 12px;
  flex-shrink: 0;
}
</style> 