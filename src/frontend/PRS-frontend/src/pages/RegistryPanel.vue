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
             Registration
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
        <v-tab value="patient">
          <v-icon left>mdi-account</v-icon>
          Patient
        </v-tab>

        <v-tab value="add">
          <v-icon left>mdi-plus-box</v-icon>
          Add visit
        </v-tab>

        <v-tab value="list">
          <v-icon left>mdi-format-list-bulleted</v-icon>
          Visits
        </v-tab>

        <v-tab value="doctor">
          <v-icon left>mdi-doctor</v-icon>
          Doctor
        </v-tab>

        <v-tab value="schedule">
          <v-icon left>mdi-calendar-clock</v-icon>
          Schedules
        </v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Patient Registration -->
        <v-window-item value="patient">
          <PatientHandler
            @patient-deleted="loadPatients"
            @patient-added="loadPatients"
            @patient-updated="loadPatients"
            :patients="patients"
          />
        </v-window-item>

        <!-- Add Visit -->
        <v-window-item value="add">
          <v-card flat class="pa-4 mt-4">
            <v-form ref="appointmentForm" @submit.prevent="addAppointment">
              <v-row>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.doctor"
                    :items="doctorsToEdit"
                    item-title="fullName"
                    item-value="fullName"
                    label="Lekarz"
                    :rules="doctorRules"
                    required
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.patient"
                    :items="patients"
                    item-title="fullName"
                    item-value="fullName"
                    label="Pacjent"
                    :rules="patientRules"
                    required
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-date-input
                    v-model="newAppointment.date"
                    label="Data wizyty"
                    :rules="dateRules"
                    placeholder="dd/mm/yyyy"
                    :display-format="dateFormat"
                    :first-day-of-week="1"
                    locale="pl"
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-menu
                    v-model="timePicker"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    max-width="290"
                    min-width="290"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        v-bind="attrs"
                        v-on="on"
                        v-model="newAppointment.time"
                        label="Godzina"
                        prepend-icon="mdi-clock-time-four-outline"
                        :rules="timeRules"
                        hint="HH:mm"
                        persistent-hint
                      />
                    </template>
                    <v-time-picker
                      v-model="newAppointment.time"
                      format="24hr"
                      :allowed-minutes="allowedMinutes"
                      @change="timePicker = false"
                    />
                  </v-menu>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="newAppointment.notes"
                    label="Description"
                  />
                </v-col>

                <v-col cols="12">
                  <v-btn type="submit" color="primary">Dodaj wizytę</v-btn>
                  <v-btn class="ml-2" @click="resetForm">Wyczyść</v-btn>
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
              :items="appointments"
              :items-per-page="10"
              class="elevation-1"
            >
              <template v-slot:item.actions="{ item }">
                <v-icon color="green" class="mr-2" @click="editAppointment(item)">mdi-pencil</v-icon>
                <v-icon color="red" @click="deleteAppointment(item)">mdi-delete</v-icon>
              </template>
            </v-data-table>
          </v-card>
        </v-window-item>

        <!-- Doctor Registration -->
        <v-window-item value="doctor">
          <v-card flat class="pa-4 mt-4">
            <v-form ref="doctorForm" @submit.prevent="addDoctor">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newDoctor.firstName"
                    label="First Name"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newDoctor.lastName"
                    label="Last Name"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newDoctor.licenseNumber"
                    label="Medical License Number"
                    :rules="licenseRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newDoctor.officeId"
                    label="Room Number"
                    :rules="roomRules"
                    required
                    type="number"
                  />
                </v-col>
                <v-col cols="12">
                  <v-btn type="submit" color="primary">Register Doctor</v-btn>
                  <v-btn class="ml-2" @click="resetDoctorForm">Clear</v-btn>
                </v-col>
              </v-row>
            </v-form>

            <v-card flat class="pa-4 mt-4">
              <v-data-table
                :headers="doctorHeaders"
                :items="doctorsToEdit"
                :items-per-page="10"
                class="elevation-1"
              >
                <template v-slot:item.actions="{ item }">
                  <v-icon color="green" class="mr-2" @click="editDoctor(item)">mdi-pencil</v-icon>
                  <v-icon color="red" @click="deleteDoctor(item)">mdi-delete</v-icon>
                </template>
              </v-data-table>
            </v-card>

          </v-card>
        </v-window-item>
        <v-window-item value="schedule">
          <DoctorSchedules :doctorsToEdit="doctorsToEdit"/>
        </v-window-item>
      </v-window>

      <!-- Edit Visit Dialog -->
      <v-dialog v-model="editDialog" max-width="600">
        <v-card>
          <v-card-title>Edytuj wizytę</v-card-title>
          <v-card-text>
            <v-form ref="editForm">
              <v-row>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="editedAppointment.doctor"
                    :items="doctorsToEdit"
                    item-text="fullName"
                    item-value="id"
                    label="Lekarz"
                    :rules="doctorRules"
                    required
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="editedAppointment.patient"
                    label="Pacjent"
                    :rules="patientRules"
                    required
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-date-input
                    v-model="editedAppointment.date"
                    label="Data wizyty"
                    :rules="dateRules"
                    placeholder="dd/mm/yyyy"
                    :display-format="dateFormat"
                    :first-day-of-week="1"
                    locale="pl"
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-menu
                    v-model="editTimePicker"
                    :close-on-content-click="false"
                    transition="scale-transition"
                    offset-y
                    max-width="290"
                    min-width="290"
                  >
                    <template v-slot:activator="{ on, attrs }">
                      <v-text-field
                        v-bind="attrs"
                        v-on="on"
                        v-model="editedAppointment.time"
                        label="Godzina"
                        prepend-icon="mdi-clock-time-four-outline"
                        :rules="timeRules"
                        hint="HH:mm"
                        persistent-hint
                      />
                    </template>
                    <v-time-picker
                      v-model="editedAppointment.time"
                      format="24hr"
                      :allowed-minutes="allowedMinutes"
                      @change="editTimePicker = false"
                    />
                  </v-menu>
                </v-col>

                <v-col cols="12">
                  <v-textarea
                    v-model="editedAppointment.notes"
                    label="Uwagi"
                  />
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer/>
            <v-btn text @click="editDialog = false">Anuluj</v-btn>
            <v-btn color="primary" @click="updateAppointment">Zapisz</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <!-- Dialog edycji lekarza -->
      <v-dialog v-model="editDoctorDialog" max-width="500">
        <v-card>
          <v-card-title>Edit Doctor</v-card-title>
          <v-card-text>
            <v-form ref="editDoctorForm">
              <v-row>
                <v-col cols="12" md="6">
                  <label class="text-caption text--secondary">First Name</label>
                  <div class="pa-2 grey lighten-4 rounded">
                    {{ editedDoctor.firstName }}
                  </div>
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="editedDoctor.lastName"
                    label="Last Name"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="editedDoctor.licenseNumber"
                    label="License Number"
                    :rules="licenseRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="editedDoctor.officeId"
                    label="Office ID"
                    :rules="roomRules"
                    required
                    type="number"
                  />
                </v-col>
              </v-row>
            </v-form>
          </v-card-text>
          <v-card-actions>
            <v-spacer/>
            <v-btn text @click="editDoctorDialog = false">Cancel</v-btn>
            <v-btn color="primary" @click="updateDoctor">Save</v-btn>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" bottom>
        {{ snackbarText }}
      </v-snackbar>
    </v-container>
  </v-app>
</template>

<script>
import doctorService from '@/services/DoctorService'
import visitService from '@/services/VisitService.js'
import DoctorSchedules from "@/components/DoctorSchedules.vue";
import PatientHandler from "@/components/PatientHandler.vue";
import patientService from "@/services/PatientService.js";

export default {
  name: 'FancyHeader',
  components: {PatientHandler, DoctorSchedules},
  data() {
    return {
      tab: 'patient',
      timePicker: false,
      editTimePicker: false,
      newAppointment: {doctor: null, patient: '', date: null, time: '', notes: ''},
      newPatient: {
        firstName: '',
        lastName: '',
        pesel: '',
        birthDate: null,
        email: '',
        phoneNumber: ''
      },
      newDoctor: {
        firstName: '',
        lastName: '',
        licenseNumber: '',
        officeId: null
      },
      patients: [],
      doctorsToEdit: [],
      appointments: [],
      doctorRules: [v => !!v || 'Wybierz lekarza'],
      patientRules: [v => !!v || 'Wprowadź pacjenta'],
      nameRules: [
        v => !!v || 'Name is required',
        v => (v && v.length >= 2) || 'Name must be at least 2 characters'
      ],
      peselRules: [
        v => !!v || 'PESEL is required',
        v => /^\d{11}$/.test(v) || 'PESEL must be 11 digits'
      ],
      emailRules: [
        v => !!v || 'E-mail is required',
        v => /.+@.+\..+/.test(v) || 'E-mail must be valid'
      ],
      phoneRules: [
        v => !!v || 'Phone number is required',
        v => /^\d{9}$/.test(v) || 'Phone number must be 9 digits'
      ],
      licenseRules: [
        v => !!v || 'License number is required',
        v => /^[A-Za-z0-9]{5,20}$/.test(v) || 'Invalid license number format'
      ],
      roomRules: [
        v => !!v || 'Room number is required',
        v => v > 0 || 'Room number must be positive'
      ],
      dateRules: [
        v => !!v || 'Date is required',
        v => /^\d{2}\/\d{2}\/\d{4}$/.test(v) || 'Invalid date format'
      ],
      timeRules: [
        v => !!v || 'Time is required',
        v => /^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/.test(v) || 'Format HH:mm'
      ],
      scheduleRules: [
        v => !v || /^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(v) || 'Format HH:MM-HH:MM'
      ],
      days: ['Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota', 'Niedziela'],
      scheduleDialog: false,
      selectedDoctor: {id: null, fullName: '', schedule: {}},
      editDoctorDialog: false,
      editedDoctor: {
        id: null,
        firstName: '',
        lastName: '',
        licenseNumber: '',
        officeId: null
      },
      editDialog: false,
      editedAppointment: {
        id: null,
        doctor: null,
        patient: '',
        date: null,
        time: '',
        notes: ''
      },
      snackbar: false,
      snackbarText: '',
      snackbarColor: 'success',
    }
  },
  computed: {
    appointmentHeaders() {
      return [
        {title: 'ID', key: 'id', align: 'start', sortable: true},
        {title: 'Doctor', key: 'doctorName', align: 'start', sortable: true},
        {title: 'Patient', key: 'patient', align: 'start', sortable: true},
        {title: 'Date', key: 'date', align: 'start', sortable: true},
        {title: 'Description', key: 'description', align: 'start', sortable: true},
        {title: 'Actions', key: 'actions', align: 'end', sortable: false}
      ]
    },
    doctorHeaders() {
      return [
        {title: 'Doctor ID', key: 'id', align: 'start', sortable: true},
        {title: 'First Name', key: 'firstName', align: 'start', sortable: true},
        {title: 'Last Name', key: 'lastName', align: 'start', sortable: true},
        {title: 'License Number', key: 'licenseNumber', align: 'start', sortable: true},
        {title: 'Office ID', key: 'officeId', align: 'start', sortable: true},
        {title: 'Actions', key: 'actions', align: 'end', sortable: false}
      ]
    },

    allowedMinutes() {
      return m => m % 15 === 0
    }
  },
  created() {
    this.loadAppointments()
    this.loadDoctors()
    this.loadPatients()
  },
  methods: {
    async loadPatients() {
      try {
        const res = await patientService.getPatients()
        this.patients = res.data.map(d => ({
          id: d.id,
          firstname: d.firstname,
          lastname: d.lastname,
          email: d.email,
          phoneNumber: d.phoneNumber,
          identificationNumber: d.identificationNumber,
          birthDate: d.birthDate,
          fullName: `${d.firstname} ${d.lastname}`
        }))
          .sort((a, b) => a.id - b.id)
      } catch {
        this.notify('Error while patients loading', 'error')
      }
    },
    async loadAppointments() {
      try {
        const res = await visitService.getVisits()
        this.appointments = res.data.map(item => ({
          ...item,
          date: this.formatDateTime(item.date)
        }))
          .sort((a, b) => a.id - b.id)
      } catch {
        this.notify('Błąd ładowania wizyt', 'error')
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
        this.notify('Błąd ładowania lekarzy', 'error')
      }
    },

    async addDoctor() {
      try {
        const doctorDto = {
          firstName: this.newDoctor.firstName,
          lastName: this.newDoctor.lastName,
          licenseNumber: this.newDoctor.licenseNumber,
          officeId: this.newDoctor.officeId
        };
        await doctorService.addDoctor(doctorDto);
        this.notify('Doctor registered successfully!', 'success');
        this.resetDoctorForm();
        this.loadDoctors();
      } catch (error) {
        this.notify('Error registering doctor: ' + (error.response?.data?.message || error.message), 'error');
      }
    },
    editDoctor(item) {
      this.editedDoctor = {
        id: item.id,
        firstName: item.firstName,
        lastName: item.lastName,
        licenseNumber: item.licenseNumber,
        officeId: item.officeId
      };
      this.editDoctorDialog = true;
    },

    async updateDoctor() {
      try {
        const dto = {
          id: this.editedDoctor.id,
          firstName: this.editedDoctor.firstName,
          lastName: this.editedDoctor.lastName,
          licenseNumber: this.editedDoctor.licenseNumber,
          officeId: this.editedDoctor.officeId
        };
        await doctorService.updateDoctor(dto);
        this.notify('Doctor updated successfully!', 'success');
        this.editDoctorDialog = false;
        this.loadDoctors();
      } catch (error) {
        this.notify('Error updating doctor: ' + (error.response?.data?.message || error.message), 'error');
      }
    },

    async deleteDoctor(item) {
      try {
        await doctorService.delete(item.id)
        this.notify('Doctor removed', 'success')
        await this.loadDoctors()
      } catch (error) {
        console.error('Error deleting doctor:', error)
        this.notify('Error deleting doctor', 'error')
      }
    },
    async addAppointment() {
      try {
        const dateTime = `${this.formatDateForBackend(this.newAppointment.date)}T${this.newAppointment.time}:00`
        const visitDto = {
          doctorName: this.newAppointment.doctor,
          patient: this.newAppointment.patient,
          date: dateTime,
          description: this.newAppointment.notes
        }
        await visitService.addVisit(visitDto)
        this.notify('Wizyta dodana pomyślnie!', 'success')
        this.resetForm()
        this.loadAppointments()
      } catch (error) {
        this.notify('Błąd podczas dodawania wizyty: ' + (error.response?.data?.message || error.message), 'error')
      }
    },

    editAppointment(item) {
      const date = new Date(item.date);
      this.editedAppointment = {
        id: item.id,
        doctor: item.doctorName,
        patient: item.patient,
        date: date,
        time: `${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`,
        notes: item.description
      };
      this.editDialog = true;
    },

    async updateAppointment() {
      try {
        const dateTime = `${this.formatDateForBackend(this.editedAppointment.date)}T${this.editedAppointment.time}:00`;
        const visitDto = {
          id: this.editedAppointment.id,
          doctorName: this.editedAppointment.doctor,
          patient: this.editedAppointment.patient,
          date: dateTime,
          description: this.editedAppointment.notes
        };
        await visitService.update(visitDto);
        this.notify('Wizyta zaktualizowana pomyślnie!', 'success');
        this.editDialog = false;
        this.loadAppointments();
      } catch (error) {
        this.notify('Błąd podczas aktualizacji wizyty: ' + (error.response?.data?.message || error.message), 'error');
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

    resetForm() {
      this.newAppointment = {doctor: null, patient: '', date: null, time: '', notes: ''}
      this.$refs.appointmentForm.resetValidation()
    },

    resetPatientForm() {
      this.newPatient = {
        firstName: '',
        lastName: '',
        pesel: '',
        birthDate: null,
        email: '',
        phoneNumber: ''
      };
      this.$refs.patientForm.resetValidation();
    },

    resetDoctorForm() {
      this.newDoctor = {
        firstName: '',
        lastName: '',
        licenseNumber: '',
        roomNumber: null
      };
      this.$refs.doctorForm.resetValidation();
    },

    async deleteAppointment(item) {
      try {
        await visitService.delete(item.id)
        this.notify('Wizyta usunięta', 'success')
        await this.loadAppointments()
      } catch (error) {
        console.error('Błąd usuwania wizyty:', error)
        this.notify('Nie udało się usunąć wizyty', 'error')
      }
    },

    notify(text, color) {
      this.snackbarText = text
      this.snackbarColor = color
      this.snackbar = true
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

  }
}
</script>
<style scoped>

.fancy-toolbar {
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  display: flex;
  align-items: center;
}

.shimmer-overlay {
  position: absolute;
  top: 0;
  left: -100%;
  width: calc(100% + 60px);
  height: 100%;
  background: rgba(255, 255, 255, 0.15);
  transform: skewX(-20deg);
  animation: shimmer-slide 2.5s ease-out forwards;
  pointer-events: none;
  z-index: 1;
  overflow: hidden;
}

.shimmer-overlay::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 45px;
  height: 100%;
  background: linear-gradient(
    90deg,
    #0066b1 0px, #0066b1 10px,
    #002d72 15px, #002d72 27px,
    #e21a1c 33px, #e21a1c 45px
  );
}

@keyframes shimmer-slide {
  0% {
    left: -100%;
  }
  100% {
    left: 95%;
  }
}

.stripe-container {
  position: absolute;
  right: 16px;
  bottom: 8px;
  display: flex;
  gap: 4px;
  z-index: 2;
}

.stripe {
  width: 8px;
  height: 24px;
  transform: rotate(20deg);
}

.custom-tabs {

  background: linear-gradient(#5d3997, #484951);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.custom-tabs .v-tab {
  color: rgba(255, 255, 255, 0.7);
  font-weight: 500;
  text-transform: none;
  transition: color 0.3s;
  padding: 12px 20px;
}

.custom-tabs .v-tab--active {
  color: #ffffff;
}

.custom-tabs .v-tabs-slider {
  height: 4px !important;
  border-radius: 2px;
  background: #ffeb3b !important;
}

.custom-tabs .v-icon {
  font-size: 25px;
  margin-right: 8px;
}
</style>
