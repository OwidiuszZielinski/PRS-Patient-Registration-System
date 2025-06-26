<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h1 class="register-title">Patient Registration</h1>

        <!-- komunikat o błędzie globalnym -->
        <div v-if="errorMessage" class="error-message">
          {{ errorMessage }}
        </div>

        <!-- komunikat o sukcesie -->
        <div v-if="successMessage" class="success-message">
          {{ successMessage }}
        </div>

        <v-form ref="registerForm" @submit.prevent="handleRegister" lazy-validation>
          <div class="form-row">
            <div class="input-field">
              <v-text-field
                v-model="formData.username"
                label="Username"
                :rules="usernameRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
            <div class="input-field">
              <v-text-field
                v-model="formData.email"
                label="Email"
                :rules="emailRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
          </div>
          <div class="form-row">
            <div class="input-field">
              <v-text-field
                v-model="formData.firstName"
                label="First Name"
                :rules="nameRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
            <div class="input-field">
              <v-text-field
                v-model="formData.lastName"
                label="Last Name"
                :rules="nameRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
          </div>
          <div class="form-row">
            <div class="input-field">
              <v-text-field
                v-model="formData.phoneNumber"
                label="Phone Number"
                :rules="phoneRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
            <div class="input-field">
              <v-text-field
                v-model="formData.identificationNumber"
                label="PESEL"
                :rules="peselRules"
                required
                autocomplete="off"
                outlined
                dense
              />
            </div>
          </div>
          <div class="input-field date-field">
            <input
              type="date"
              v-model="formData.birthDate"
              required
              autocomplete="off"
              class="v-input__control v-input__slot"
              style="width:100%;height:56px;background:#2a2a2a;color:#fff;border:1px solid rgba(118,74,188,0.5);border-radius:0;padding:0 15px;"
            >
          </div>
          <div class="form-row password-row">
            <div class="input-field">
              <v-text-field
                :type="showPassword ? 'text' : 'password'"
                v-model="formData.password"
                label="Password"
                :rules="passwordRules"
                required
                autocomplete="new-password"
                outlined
                dense
                :append-icon="showPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append="showPassword = !showPassword"
              />
            </div>
            <div class="input-field">
              <v-text-field
                :type="showConfirmPassword ? 'text' : 'password'"
                v-model="formData.confirmPassword"
                label="Confirm Password"
                :rules="confirmPasswordRules"
                required
                autocomplete="new-password"
                outlined
                dense
                :append-icon="showConfirmPassword ? 'mdi-eye' : 'mdi-eye-off'"
                @click:append="showConfirmPassword = !showConfirmPassword"
              />
            </div>
          </div>
          <button type="submit" class="register-button" :disabled="isLoading">
            {{ isLoading ? 'Creating Account...' : 'Create Account' }}
          </button>
        </v-form>

        <div class="login-link">
          <span>Already have an account?</span>
          <a @click="$router.push('/login')" style="cursor: pointer;">Sign in</a>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'Register',
  data() {
    return {
      formData: {
        username: '',
        email: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
        identificationNumber: '',
        birthDate: '',
        password: '',
        confirmPassword: ''
      },
      showPassword: false,
      showConfirmPassword: false,
      isLoading: false,
      errorMessage: '',
      successMessage: '',
      usernameRules: [v => !!v || 'Username is required', v => (v && v.length >= 2) || 'Username must be at least 2 characters'],
      nameRules: [v => !!v || 'Name is required', v => (v && v.length >= 2) || 'Name must be at least 2 characters'],
      peselRules: [v => !!v || 'PESEL is required', v => /^\d{11}$/.test(v) || 'PESEL must be 11 digits'],
      emailRules: [v => !!v || 'E-mail is required', v => /.+@.+\..+/.test(v) || 'E-mail must be valid'],
      phoneRules: [v => !!v || 'Phone number is required', v => /^\d{9}$/.test(v) || 'Phone number must be 9 digits'],
      passwordRules: [v => !!v || 'Password is required', v => (v && v.length >= 6) || 'Password must be at least 6 characters'],
      confirmPasswordRules: [v => !!v || 'Confirm your password', v => v === this.formData.password || 'Passwords do not match']
    }
  },
  watch: {
    'formData.password'(val) {
      // Trigger confirm password validation on password change
      this.$refs.registerForm && this.$refs.registerForm.validate()
    }
  },
  methods: {
    async handleRegister() {
      if (!this.$refs.registerForm.validate()) return
      this.isLoading = true
      this.errorMessage = ''
      this.successMessage = ''
      try {
        await axios.post('http://localhost:8080/api/v1/auth/register', {
          username: this.formData.username,
          email: this.formData.email,
          password: this.formData.password,
          role: 'PATIENT',
          firstName: this.formData.firstName,
          lastName: this.formData.lastName,
          phoneNumber: this.formData.phoneNumber,
          identificationNumber: this.formData.identificationNumber,
          birthDate: this.formData.birthDate
        })
        this.successMessage = 'Account created successfully! You can now sign in.'
        setTimeout(() => {
          this.$router.push('/login')
        }, 2000)
      } catch (error) {
        console.error('Registration error:', error)
        if (error.response && error.response.status === 400) {
          this.errorMessage = error.response.data || 'Registration failed. Please check your data.'
        } else if (error.response && error.response.status === 409) {
          this.errorMessage = 'Username or email already exists'
        } else {
          this.errorMessage = 'Registration failed. Please try again.'
        }
      } finally {
        this.isLoading = false
      }
    }
  }
}
</script>

<style scoped>
.register-page {
  background-color: #1e1f22;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-container {
  width: 100%;
  max-width: 600px;
}

.register-card {
  background-color: #212121;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  width: 100%;
}

.register-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
  text-align: center;
  margin-bottom: 30px;
}

.register-form {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
  margin-bottom: 20px;
}

.input-field {
  position: relative;
  margin-bottom: 0px;
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

.date-field {
  margin-bottom: 24px;
}

.password-row {
  margin-bottom: 20px;
}

.register-button {
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
  margin-top: 20px;
}

.register-button:hover:not(:disabled) {
  background-color: #5d3997;
}

.register-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  font-size: 0.875rem;
  color: #ffffff;
  margin-top: 20px;
}

.login-link a {
  color: #764abc;
  font-weight: 700;
  text-decoration: none;
  margin-left: 5px;
}

.login-link a:hover {
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

.success-message {
  background: #e8f5e8;
  color: #2e7d32;
  border: 1px solid #c8e6c9;
  padding: 10px 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  font-weight: 500;
  text-align: center;
}

.field-error {
  color: #ff5252;
  font-size: 0.9em;
  margin-top: 2px;
  margin-bottom: 8px;
}

@media (max-width: 768px) {
  .form-row {
    grid-template-columns: 1fr;
  }
}
</style>
