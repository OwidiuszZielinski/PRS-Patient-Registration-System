<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card class="text-center pa-6">
          <v-card-title class="text-h4 mb-4">
            <v-icon 
              :color="status === 'success' ? 'success' : 'error'" 
              size="64"
              class="mb-4"
            >
              {{ status === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle' }}
            </v-icon>
            <div class="mt-4">
              {{ status === 'success' ? 'Payment Successful!' : 'Payment Failed' }}
            </div>
          </v-card-title>
          
          <v-card-text>
            <div v-if="status === 'success'" class="text-center">
              <p class="text-h6 success--text mb-4">Payment Completed Successfully!</p>
              <p>Your visit has been registered and payment has been processed.</p>
              <p class="text-caption">Payment ID: {{ paymentId }}</p>
              <p class="text-body-1 mb-6">
                You will be redirected to your visits list in {{ countdown }} seconds.
              </p>
            </div>
            
            <div v-else-if="status === 'failure'" class="text-center">
              <p class="text-h6 error--text mb-4">Payment Failed</p>
              <p>Unfortunately, the payment could not be processed.</p>
              <p class="text-caption">Payment ID: {{ paymentId }}</p>
              <p class="text-body-1 mb-6">
                You can return to the visit registration page to try again.
              </p>
            </div>
            
            <div v-else class="text-center">
              <v-progress-circular indeterminate color="primary" class="mb-4"></v-progress-circular>
              <p class="text-h6 mb-4">Processing Payment...</p>
              <p>Please wait while we verify your payment status.</p>
            </div>
          </v-card-text>
          
          <v-card-actions class="justify-center">
            <v-btn 
              v-if="status === 'success'" 
              color="success" 
              size="large"
              @click="goToVisits"
            >
              View My Visits
            </v-btn>
            <v-btn 
              v-else-if="status === 'failure'" 
              color="primary" 
              size="large"
              @click="goToRegistration"
            >
              Try Again
            </v-btn>
            <v-btn 
              v-else 
              color="primary" 
              size="large"
              disabled
            >
              Processing...
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  name: 'PaymentStatus',
  data() {
    return {
      status: 'success',
      paymentId: '',
      countdown: 5
    }
  },
  mounted() {
    // Get status from URL parameters
    const urlParams = new URLSearchParams(window.location.search)
    this.status = urlParams.get('status') || 'success'
    this.paymentId = urlParams.get('paymentId') || ''
    
    // Auto-redirect after 5 seconds for success
    if (this.status === 'success') {
      this.startCountdown()
    }
  },
  methods: {
    startCountdown() {
      const timer = setInterval(() => {
        this.countdown--
        if (this.countdown <= 0) {
          clearInterval(timer)
          this.goToVisits()
        }
      }, 1000)
    },
    
    goToVisits() {
      this.$router.push('/patient-view?tab=list')
    },
    
    goToRegistration() {
      this.$router.push('/patient-view?tab=add')
    }
  }
}
</script>

<style scoped>
.v-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}
</style> 