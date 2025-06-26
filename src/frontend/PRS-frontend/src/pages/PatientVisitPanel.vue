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
                  <v-btn type="submit" color="primary">
                    {{ parseFloat(totalCost) > 0 ? 'Register and Pay' : 'Register' }}
                  </v-btn>
                  <v-btn class="ml-2" @click="resetForm">Clear</v-btn>
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
    </v-container>
  </v-app>
</template>

<script>
import doctorService from '@/services/DoctorService'
import visitService from '@/services/VisitService.js'
import serviceService from '@/services/ServiceService.js'
import axios from 'axios'

export default {
  name: 'PatientVisitPanel',
  data() {
    return {
      tab: 'add',
      timePicker: false,
      newAppointment: {doctor: null, patient: '', date: null, time: '', notes: ''},
      doctorsToEdit: [],
      myAppointments: [],
      currentPatientName: '',
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
      isPatient: false,
      availableServices: [],
      selectedServices: [],
    }
  },
  async mounted() {
    await this.loadDoctors()
    await this.loadMyAppointments()
    await this.setCurrentPatientName()
    await this.loadAvailableServices()
  },
  methods: {
    async setCurrentPatientName() {
      const email = localStorage.getItem('email')
      const role = localStorage.getItem('role')

      if (role === 'PATIENT' && email) {
        try {
          const response = await axios.get(`http://localhost:8080/api/patient/by-email/${email}`)
          const patient = response.data
          if (patient && patient.firstname && patient.lastname) {
            this.currentPatientName = `${patient.firstname} ${patient.lastname}`
            this.newAppointment.patient = this.currentPatientName
            this.isPatient = true
          } else {
            this.currentPatientName = ''
            this.newAppointment.patient = ''
            this.isPatient = false
          }
        } catch (error) {
          this.currentPatientName = ''
          this.newAppointment.patient = ''
          this.isPatient = false
        }
      } else {
        this.currentPatientName = ''
        this.newAppointment.patient = ''
        this.isPatient = false
      }
    },

    async loadMyAppointments() {
      try {
        const res = await visitService.getVisits()
        const email = localStorage.getItem('email')
        const role = localStorage.getItem('role')

        // Filter appointments based on user role
        if (role === 'PATIENT') {
          // For patients, show only their own visits
          this.myAppointments = res.data
            .filter(item => item.patient === email || item.patient === this.currentPatientName)
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
      } catch {
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
      try {
        const dateTime = `${this.formatDateForBackend(this.newAppointment.date)}T${this.newAppointment.time}:00`
        const visitDto = {
          doctorName: this.newAppointment.doctor,
          patient: this.newAppointment.patient,
          date: dateTime,
          description: this.newAppointment.notes,
          selectedServices: this.selectedServices,
          totalCost: this.totalCost
        }
        await visitService.addVisit(visitDto)
        this.notify('Visit registered successfully!', 'success')
        this.resetForm()
        this.loadMyAppointments()
      } catch (error) {
        this.notify('Error while registering visit: ' + (error.response?.data?.message || error.message), 'error')
      }
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
      } else {
        this.newAppointment = {doctor: null, patient: '', date: null, time: '', notes: ''}
      }
      this.selectedServices = []
      this.$refs.appointmentForm.resetValidation()
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
