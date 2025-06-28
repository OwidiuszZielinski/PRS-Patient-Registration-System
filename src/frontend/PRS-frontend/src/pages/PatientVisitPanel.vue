<template>
  <v-app>
    <v-container>
      <!-- Header -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-toolbar color="#764ABC" dark flat elevation="2" class="fancy-toolbar">
            <!-- One-time shimmer overlay -->
            <div class="shimmer-overlay"></div>

            <v-toolbar-title class="header-content">
              Patient Visit Registration
            </v-toolbar-title>

            <!-- Persistent BMW M stripes at end of toolbar -->
            <div class="stripe-container">
              <span class="stripe blue"></span>
              <span class="stripe navy"></span>
              <span class="stripe red"></span>
            </div>

            <v-spacer/>
          </v-toolbar>
        </v-col>
      </v-row>

      <!-- Tabs -->
      <v-tabs
        v-model="tab"
        class="custom-tabs"
        grow
        background-color="transparent"
      >
        <v-tab value="add">
          <v-icon left>mdi-plus-box</v-icon>
          Register Visit
        </v-tab>

        <v-tab value="list">
          <v-icon left>mdi-format-list-bulleted</v-icon>
          My Visits
        </v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Add Visit -->
        <v-window-item value="add">
          <v-card flat class="pa-4 mt-4">
            <!-- Patient Info Alert -->
            <v-alert
              v-if="isPatient"
              type="info"
              dense
              border="left"
              colored-border
              class="mb-4"
            >
              <div class="d-flex align-center">
                <v-icon class="mr-2">mdi-account-check</v-icon>
                <span>You are registered as: <strong>{{ currentPatientName }}</strong></span>
              </div>
            </v-alert>

            <v-form ref="appointmentForm" @submit.prevent="addAppointment">
              <v-row>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.doctor"
                    :items="doctorsToEdit"
                    item-title="fullName"
                    item-value="fullName"
                    label="Doctor"
                    :rules="doctorRules"
                    required
                  />
                </v-col>


                <!-- Date of visit -->
                <v-col cols="12" md="6">
                  <v-date-input
                    v-model="newAppointment.date"
                    label="Date of visit"
                    :rules="dateRules"
                    placeholder="dd/mm/yyyy"
                    :display-format="dateFormat"
                    :first-day-of-week="1"
                    locale="en"
                    :min="today"
                  />
                </v-col>

                <!-- Time of visit -->
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newAppointment.time"
                    :active="timePicker"
                    :focused="timePicker"
                    label="Select Time"
                    placeholder="HH:mm"
                    prepend-icon="mdi-clock-time-four-outline"
                    @click:prepend="timePicker = true"
                    readonly
                    :rules="timeRules"
                  >
                    <v-dialog
                      v-model="timePicker"
                      activator="parent"
                      width="290"
                    >
                      <v-time-picker
                        v-if="timePicker"
                        v-model="newAppointment.time"
                        @update:minute="timePicker = false"
                        format="24hr"
                        locale="en"
                      />
                    </v-dialog>
                  </v-text-field>
                </v-col>

                <!-- Description -->
                <v-col cols="12">
                  <v-textarea
                    v-model="newAppointment.notes"
                    label="Description"
                    placeholder="Please describe your symptoms or reason for visit"
                  />
                </v-col>

                <!-- Additional Services -->
                <v-col cols="12">
                  <v-card outlined class="pa-4">
                    <v-card-title class="text-h6">
                      <v-icon left>mdi-stethoscope</v-icon>
                      Additional Services
                    </v-card-title>
                    <v-card-text>
                      <v-row>
                        <v-col
                          v-for="service in availableServices"
                          :key="service.id"
                          cols="12"
                          md="6"
                          lg="4"
                        >
                          <v-checkbox
                            v-model="selectedServices"
                            :value="service"
                            :label="`${service.name} - ${service.price} PLN`"
                            :hint="service.description"
                            persistent-hint
                            color="primary"
                          />
                        </v-col>
                      </v-row>
                      <v-divider class="my-4" />
                      <div class="text-h6 text-right">
                        Total Cost: <span class="font-weight-bold primary--text">{{ totalCost }} PLN</span>
                      </div>
                    </v-card-text>
                  </v-card>
                </v-col>

                <v-col cols="12">
                  <v-btn
                    type="submit"
                    color="primary"
                    :loading="isProcessingPayment"
                    :disabled="isProcessingPayment"
                  >
                    <v-icon left v-if="!isProcessingPayment">
                      {{ parseFloat(totalCost) > 0 ? 'mdi-credit-card' : 'mdi-calendar-check' }}
                    </v-icon>
                    {{ parseFloat(totalCost) > 0 ? 'Register and Pay' : 'Register' }}
                  </v-btn>
                  <v-btn class="ml-2" @click="resetForm" :disabled="isProcessingPayment">Clear</v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card>
        </v-window-item>

        <!-- Visit List -->
        <v-window-item value="list">
          <v-card flat class="pa-4 mt-4">
            <v-data-table
              :headers="appointmentHeaders"
              :items="myAppointments"
              :items-per-page="10"
              class="elevation-1"
            >
              <template v-slot:item.selectedServices="{ item }">
                <div v-if="item.selectedServices && item.selectedServices.length > 0">
                  <v-chip
                    v-for="service in item.selectedServices"
                    :key="service.id"
                    small
                    color="primary"
                    class="mr-1 mb-1"
                  >
                    {{ service.name }}
                  </v-chip>
                </div>
                <span v-else class="text-grey">No services</span>
              </template>

              <template v-slot:item.totalCost="{ item }">
                <span class="font-weight-bold">{{ item.totalCost || 0 }} PLN</span>
              </template>

              <template v-slot:item.actions="{ item }">
                <v-icon color="red" @click="deleteAppointment(item)">mdi-delete</v-icon>
              </template>
            </v-data-table>
          </v-card>
        </v-window-item>
      </v-window>

      <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" bottom>
        {{ snackbarText }}
      </v-snackbar>

      <!-- Payment Status Dialog -->
      <v-dialog v-model="paymentDialog" persistent max-width="400">
        <v-card>
          <v-card-title class="text-h5">
            <v-icon left :color="paymentStatus === 'success' ? 'success' : 'error'">
              {{ paymentStatus === 'success' ? 'mdi-check-circle' : 'mdi-alert-circle' }}
            </v-icon>
            Payment Status
          </v-card-title>

          <v-card-text>
            <div v-if="paymentStatus === 'success'" class="text-center">
              <p class="text-h6 success--text mb-4">Payment Completed Successfully!</p>
              <p>Your visit has been registered and payment has been processed.</p>
              <p class="text-caption">You will be redirected to your visits list.</p>
            </div>

            <div v-else-if="paymentStatus === 'failure'" class="text-center">
              <p class="text-h6 error--text mb-4">Payment Failed</p>
              <p>Unfortunately, the payment could not be processed.</p>
              <p class="text-caption">Please try registering your visit again.</p>
            </div>

            <div v-else class="text-center">
              <v-progress-circular indeterminate color="primary" class="mb-4"></v-progress-circular>
              <p class="text-h6 mb-4">Processing Payment...</p>
              <p>Please wait while we verify your payment status.</p>
            </div>
          </v-card-text>

          <v-card-actions class="justify-center">
            <v-btn
              v-if="paymentStatus === 'success'"
              color="success"
              @click="handlePaymentSuccess"
            >
              Continue
            </v-btn>
            <v-btn
              v-else-if="paymentStatus === 'failure'"
              color="error"
              @click="handlePaymentFailure"
            >
              Try Again
            </v-btn>
            <v-btn
              v-else
              color="primary"
              disabled
            >
              Processing...
            </v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </v-app>
</template>

<script>
import doctorService from '@/services/DoctorService'
import visitService from '@/services/VisitService.js'
import serviceService from '@/services/ServiceService.js'
import paymentService from '@/services/PaymentService.js'
import axios from 'axios'

export default {
  name: 'PatientVisitPanel',
  data() {
    const email = localStorage.getItem('email')
    const role = localStorage.getItem('role')
    let initialPatientName = ''

    // Try to get patient name from localStorage if available
    if (role === 'PATIENT' && email) {
      // For now, use email as fallback, will be updated in setCurrentPatientName()
      initialPatientName = email
    }

    return {
      tab: localStorage.getItem('patientVisitTab') || 'add',
      timePicker: false,
      newAppointment: {doctor: null, patient: initialPatientName, date: null, time: '', notes: ''},
      doctorsToEdit: [],
      myAppointments: [],
      currentPatientName: initialPatientName,
      doctorRules: [v => !!v || 'Choose a doctor'],
      patientRules: [
        v => this.isPatient || !!v || 'Patient is required'
      ],
      dateRules: [
        v => !!v || 'Date is required',
        v => /^\d{2}\/\d{2}\/\d{4}$/.test(v) || 'Invalid date format'
      ],
      today: new Date().toISOString().substr(0,10),
      timeRules: [
        v => !!v || 'Time is required',
        v => /^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/.test(v) || 'Format HH:mm'
      ],
      snackbar: false,
      snackbarText: '',
      snackbarColor: 'success',
      isPatient: role === 'PATIENT',
      availableServices: [],
      selectedServices: [],
      paymentDialog: false,
      paymentStatus: '',
      currentPaymentId: null,
      isProcessingPayment: false,
      paymentCheckAttempts: 0,
      isInitialized: false,
    }
  },
  async mounted() {
    console.log('PatientVisitPanel mounted, initial tab:', this.tab)

    // Load patient name first so it's available for filtering visits
    await this.setCurrentPatientName()
    console.log('After setCurrentPatientName, currentPatientName:', this.currentPatientName)

    await this.loadDoctors()
    await this.loadMyAppointments()
    await this.loadAvailableServices()

    // Check URL parameters for tab override
    const urlParams = new URLSearchParams(window.location.search)
    const tabParam = urlParams.get('tab')
    console.log('URL tab parameter:', tabParam)
    if (tabParam && (tabParam === 'add' || tabParam === 'list')) {
      this.tab = tabParam
      localStorage.setItem('patientVisitTab', tabParam)
      console.log('Tab set from URL parameter to:', tabParam)
    }

    console.log('Final tab value:', this.tab)

    if (!this.isInitialized) {
      this.checkPaymentStatusFromUrl()
      this.isInitialized = true
    }
  },
  beforeRouteLeave(to, from, next) {
    // Clear form data when leaving the page
    this.resetForm()
    // Only reset isInitialized if actually navigating away, not on refresh
    if (to.path !== from.path) {
      this.isInitialized = false
    }
    next()
  },
  methods: {
    checkPaymentStatusFromUrl() {
      const urlParams = new URLSearchParams(window.location.search)
      const status = urlParams.get('status')
      const paymentId = urlParams.get('paymentId')
      const tabParam = urlParams.get('tab')

      // Handle tab parameter only if it exists
      if (tabParam && (tabParam === 'add' || tabParam === 'list')) {
        this.tab = tabParam
        localStorage.setItem('patientVisitTab', tabParam)
      }

      // Clear URL parameters immediately to prevent duplicate calls
      if (status || paymentId || tabParam) {
        window.history.replaceState({}, document.title, window.location.pathname)
      }

      if (status && paymentId) {
        this.currentPaymentId = paymentId
        this.paymentStatus = status
        this.paymentDialog = true
      } else if (paymentId) {
        // If only paymentId is present (from PayU redirect), check payment status
        // Note: paymentId in URL is actually visitId
        this.checkPaymentAndCreateVisit(paymentId)
      }
    },

    async checkPaymentAndCreateVisit(visitId) {
      try {
        console.log('Checking payment and creating visit for visitId:', visitId)

        const response = await visitService.checkPaymentAndCreateVisit(visitId)
        console.log('Payment check response:', response.data)

        if (response.data.status === 'SUCCESS') {
          this.notify('Payment completed and visit created successfully!', 'success')
          await this.loadMyAppointments()
          this.tab = 'list'
        } else if (response.data.status === 'PENDING') {
          if (!this.paymentCheckAttempts) {
            this.paymentCheckAttempts = 0
          }

          if (this.paymentCheckAttempts < 30) {
            this.paymentCheckAttempts++
            this.notify(`Payment is still being processed... (attempt ${this.paymentCheckAttempts}/30)`, 'info')
            setTimeout(() => this.checkPaymentAndCreateVisit(visitId), 2000)
          } else {
            this.notify('Payment check timeout. Please contact support if payment was completed.', 'warning')
            this.paymentCheckAttempts = 0
          }
        } else if (response.data.status === 'ERROR') {
          this.notify('Payment error: ' + (response.data.message || 'Unknown error'), 'error')
          console.error('Payment error:', response.data)
          this.paymentCheckAttempts = 0
        } else {
          this.notify('Payment status unknown: ' + response.data.status, 'warning')
          console.warn('Unknown payment status:', response.data)
          this.paymentCheckAttempts = 0
        }
      } catch (error) {
        console.error('Error checking payment status:', error)
        this.notify('Error checking payment status: ' + (error.response?.data?.message || error.message), 'error')
        this.paymentCheckAttempts = 0
      }
    },

    async setCurrentPatientName() {
      const email = localStorage.getItem('email')
      const role = localStorage.getItem('role')

      console.log('setCurrentPatientName called with:', { email, role })

      if (role === 'PATIENT' && email) {
        try {
          const response = await axios.get(`http://localhost:8080/api/patient/by-email/${email}`)
          const patient = response.data
          console.log('Patient data from API:', patient)

          if (patient && patient.firstname && patient.lastname) {
            this.currentPatientName = `${patient.firstname} ${patient.lastname}`
            this.newAppointment.patient = this.currentPatientName
            this.isPatient = true
            console.log('Patient name set to:', this.currentPatientName)
            console.log('newAppointment.patient set to:', this.newAppointment.patient)
          } else {
            // If no patient name found, use email as fallback
            this.currentPatientName = email
            this.newAppointment.patient = email
            this.isPatient = true
            console.log('No patient name found, using email as fallback:', email)
          }
        } catch (error) {
          console.error('Error fetching patient data:', error)
          // Use email as fallback
          this.currentPatientName = email
          this.newAppointment.patient = email
          this.isPatient = true
          console.log('Error fetching patient data, using email as fallback:', email)
        }
      } else {
        this.currentPatientName = ''
        this.newAppointment.patient = ''
        this.isPatient = false
        console.log('Not a patient or no email')
      }
    },

    async loadMyAppointments() {
      try {
        const res = await visitService.getVisits()
        const email = localStorage.getItem('email')
        const role = localStorage.getItem('role')

        console.log('Loading appointments for:', { email, role, currentPatientName: this.currentPatientName })
        console.log('All visits from API:', res.data)

        if (role === 'PATIENT') {
          // For patients, show only their own visits
          const filteredVisits = res.data.filter(item => {
            const matchesEmail = item.patient === email
            const matchesName = item.patient === this.currentPatientName
            console.log(`Visit ${item.id}: patient="${item.patient}", matchesEmail=${matchesEmail}, matchesName=${matchesName}`)
            return matchesEmail || matchesName
          })

          console.log('Filtered visits for patient:', filteredVisits)

          this.myAppointments = filteredVisits
            .map(item => ({
              ...item,
              date: this.formatDateTime(item.date)
            }))
            .sort((a, b) => a.id - b.id)
        } else {
          // For other roles, show all visits
          this.myAppointments = res.data
            .map(item => ({
              ...item,
              date: this.formatDateTime(item.date)
            }))
            .sort((a, b) => a.id - b.id)
        }

        console.log('Final appointments array:', this.myAppointments)
      } catch (error) {
        console.error('Error loading visits:', error)
        this.notify('Error loading visits', 'error')
      }
    },

    async loadDoctors() {
      try {
        const res = await doctorService.getDoctors()

        this.doctorsToEdit = res.data.map(d => ({
          id: d.id,
          officeId: d.officeId,
          firstName: d.firstName,
          lastName: d.lastName,
          licenseNumber: d.licenseNumber,
          fullName: `${d.firstName} ${d.lastName}`
        }))
          .sort((a, b) => a.id - b.id)
      } catch (e) {
        this.notify('Doctor loading error', 'error')
      }
    },

    async loadAvailableServices() {
      try {
        const res = await serviceService.getServices()
        this.availableServices = res.data
      } catch (error) {
        this.notify('Error loading available services', 'error')
      }
    },

    async addAppointment() {
      if (this.isProcessingPayment) return

      // Additional check to prevent duplicate submissions
      if (this.paymentCheckAttempts > 0) return

      try {
        this.isProcessingPayment = true
        this.paymentCheckAttempts = 1 // Set flag to prevent duplicate calls

        const dateTime = `${this.formatDateForBackend(this.newAppointment.date)}T${this.newAppointment.time}:00`
        const visitDto = {
          doctorName: this.newAppointment.doctor,
          patient: this.newAppointment.patient,
          date: dateTime,
          description: this.newAppointment.notes,
          selectedServices: this.selectedServices,
          totalCost: this.totalCost
        }

        console.log('Creating visit with data:', {
          newAppointment: this.newAppointment,
          currentPatientName: this.currentPatientName,
          visitDto: visitDto
        })

        if (parseFloat(this.totalCost) > 0) {
          const paymentResponse = await paymentService.addVisitWithPayment(visitDto)

          if (paymentResponse.data.status === 'SUCCESS') {
            if (paymentResponse.data.redirectUrl) {
              if (paymentResponse.data.redirectUrl.startsWith('http://localhost:3000')) {
                this.$router.push(paymentResponse.data.redirectUrl.replace('http://localhost:3000', ''))
              } else {
                this.notify('Redirecting to PayU payment gateway...', 'info')
                setTimeout(() => {
                  window.location.href = paymentResponse.data.redirectUrl
                }, 1000)
              }
            } else {
              this.notify('Visit registered successfully!', 'success')
              this.resetForm()
              this.loadMyAppointments()
              this.tab = 'list'
            }
          } else {
            this.notify('Payment failed: ' + paymentResponse.data.message + '. Visit was not registered.', 'error')
          }
        } else {
          await visitService.addVisit(visitDto)
          this.notify('Visit registered successfully!', 'success')
          this.resetForm()
          this.loadMyAppointments()
          this.tab = 'list'
        }
      } catch (error) {
        this.notify('Error while registering visit: ' + (error.response?.data?.message || error.message), 'error')
      } finally {
        this.isProcessingPayment = false
        this.paymentCheckAttempts = 0 // Reset flag after completion
      }
    },

    async checkPaymentStatus() {
      if (!this.currentPaymentId) return

      try {
        const response = await paymentService.getPaymentStatus(this.currentPaymentId)
        const status = response.data.status

        if (status === 'COMPLETED' || status === 'SUCCESS') {
          this.notify('Payment completed successfully!', 'success')
          this.paymentDialog = false
          this.loadMyAppointments()
          this.tab = 'list'
        } else if (status === 'CANCELED' || status === 'FAILED') {
          this.notify('Payment failed or was canceled', 'error')
          this.paymentDialog = false
        } else {
          setTimeout(() => this.checkPaymentStatus(), 2000)
        }
      } catch (error) {
        this.notify('Error checking payment status', 'error')
        this.paymentDialog = false
      }
    },

    handlePaymentSuccess() {
      this.notify('Payment completed successfully!', 'success')
      this.paymentDialog = false
      this.loadMyAppointments()
      this.tab = 'list'
    },

    handlePaymentFailure() {
      this.notify('Payment failed or was canceled. Please try again.', 'error')
      this.paymentDialog = false
    },

    dateFormat(date) {
      if (!date) return ''
      const day = String(date.getDate()).padStart(2, '0')
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const year = date.getFullYear()
      return `${day}/${month}/${year}`
    },

    formatDateForBackend(date) {
      if (!date) return ''
      if (date instanceof Date) {
        const year = date.getFullYear()
        const month = String(date.getMonth() + 1).padStart(2, '0')
        const day = String(date.getDate()).padStart(2, '0')
        return `${year}-${month}-${day}`
      }
      if (typeof date === 'string' && date.includes('/')) {
        const [day, month, year] = date.split('/')
        return `${year}-${month}-${day}`
      }
      return date
    },

    formatDateTime(dateTimeString) {
      if (!dateTimeString) return ''
      const date = new Date(dateTimeString)
      const day = String(date.getDate()).padStart(2, '0')
      const month = String(date.getMonth() + 1).padStart(2, '0')
      const year = date.getFullYear()
      const hours = String(date.getHours()).padStart(2, '0')
      const minutes = String(date.getMinutes()).padStart(2, '0')
      return `${day}/${month}/${year} ${hours}:${minutes}`
    },

    resetForm() {
      const role = localStorage.getItem('role')
      if (role === 'PATIENT') {
        this.newAppointment = {doctor: null, patient: this.currentPatientName, date: null, time: '', notes: ''}
        console.log('Reset form for patient, patient set to:', this.currentPatientName)
      } else {
        this.newAppointment = {doctor: null, patient: '', date: null, time: '', notes: ''}
      }
      this.selectedServices = []
      this.timePicker = false
      this.isProcessingPayment = false
      this.paymentCheckAttempts = 0
      this.currentPaymentId = null
      this.paymentStatus = ''
      this.paymentDialog = false

      // Reset form validation
      if (this.$refs.appointmentForm) {
        this.$refs.appointmentForm.resetValidation()
      }
    },

    async deleteAppointment(item) {
      try {
        await visitService.delete(item.id)
        this.notify('Visit deleted', 'success')
        await this.loadMyAppointments()
      } catch (error) {
        console.error('Error deleting visit:', error)
        this.notify('Could not delete visit', 'error')
      }
    },

    notify(text, color) {
      this.snackbarText = text
      this.snackbarColor = color
      this.snackbar = true
    },
  },
  computed: {
    appointmentHeaders() {
      return [
        { title: 'Doctor', key: 'doctorName', sortable: true },
        { title: 'Patient', key: 'patient', sortable: true },
        { title: 'Date', key: 'date', sortable: true },
        { title: 'Description', key: 'description', sortable: false },
        { title: 'Services', key: 'selectedServices', sortable: false },
        { title: 'Total Cost', key: 'totalCost', sortable: true },
        { title: 'Actions', key: 'actions', sortable: false }
      ]
    },
    totalCost() {
      return this.selectedServices.reduce((sum, service) => sum + parseFloat(service.price), 0).toFixed(2)
    }
  },
  watch: {
    paymentDialog(newVal) {
      if (newVal && this.currentPaymentId) {
        this.checkPaymentStatus()
      }
    },
    tab(newVal, oldVal) {
      console.log(`Tab changed from ${oldVal} to ${newVal}`)
      // Save current tab to localStorage
      localStorage.setItem('patientVisitTab', newVal)
    }
  }
}
</script>

<style scoped>
.fancy-toolbar {
  position: relative;
  overflow: hidden;
}

.shimmer-overlay {
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.stripe-container {
  position: absolute;
  right: 0;
  top: 0;
  height: 100%;
  display: flex;
  align-items: stretch;
}

.stripe {
  width: 4px;
  margin-left: 2px;
}

.stripe.blue { background-color: #0066cc; }
.stripe.navy { background-color: #003366; }
.stripe.red { background-color: #cc0000; }

.header-content {
  z-index: 1;
  position: relative;
}

.custom-tabs {
  margin-bottom: 20px;
}

.readonly-field {
  background-color: #f5f5f5;
}

.readonly-field .v-field {
  background-color: #f5f5f5 !important;
}
</style>
