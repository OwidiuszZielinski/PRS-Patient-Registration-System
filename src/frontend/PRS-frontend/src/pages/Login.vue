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
      errorMessage: ''
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
</style> 