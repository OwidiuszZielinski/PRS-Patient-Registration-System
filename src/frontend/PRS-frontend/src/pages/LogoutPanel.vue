<template>
  <div class="logout-card">
    <h2 class="logout-title">Confirm Logout</h2>
    <p class="logout-message">Are you sure you want to log out?</p>

    <div class="logout-buttons">
      <button class="logout-button" @click="handleLogout">Yes, Log Out</button>
      <button class="cancel-button" @click="handleCancel">Cancel</button>
    </div>

    <p v-if="logoutSuccess" class="logout-success">Logout success!</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const logoutSuccess = ref(false)

const handleLogout = async () => {
  try {
    await fetch('http://localhost:8080/logout', {
      method: 'POST',
      credentials: 'include'
    })
    logoutSuccess.value = true
    setTimeout(() => {
      window.location.href = 'http://localhost:8080/login'
    }, 1000)
  } catch (error) {
    console.error('Logout failed', error)
  }
}


const handleCancel = () => {
  router.push('/')
}
</script>

<style scoped>
.logout-card {
  background-color: #212121;
  border-radius: 12px;
  padding: 40px;
  width: 100%;
  max-width: 500px;
  margin: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.logout-title {
  font-size: 1.75rem;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 20px;
}

.logout-message {
  font-size: 1rem;
  color: #bbbbbb;
  margin-bottom: 30px;
}

.logout-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.logout-button,
.cancel-button {
  height: 45px;
  min-width: 120px;
  border: none;
  border-radius: 28px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: background-color 0.3s;
}

.logout-button {
  background-color: #764abc;
  color: #ffffff;
}

.logout-button:hover {
  background-color: #5d3997;
}

.cancel-button {
  background-color: #444;
  color: #ffffff;
}

.cancel-button:hover {
  background-color: #333;
}

.logout-success {
  margin-top: 25px;
  font-size: 1rem;
  font-weight: bold;
  color: #00c853;
}
</style>
