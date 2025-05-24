<template>
  <v-app>
    <v-container>
      <!-- Header -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-toolbar color="#764ABC" dark flat>
            <v-toolbar-title>Registration</v-toolbar-title>
            <v-spacer/>
          </v-toolbar>
        </v-col>
      </v-row>

      <!-- Tabs -->
      <v-tabs v-model="tab" grow>
        <v-tab value="add">Add visit</v-tab>
        <v-tab value="list">Visits</v-tab>
        <v-tab value="schedule">Schedules</v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Add Visit -->
        <v-window-item value="add">
          <v-card flat class="pa-4 mt-4">
            <v-form ref="appointmentForm" @submit.prevent="addAppointment">
              <v-row>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.doctor"
                    :items="doctors"
                    item-text="fullName"
                    item-value="id"
                    label="Lekarz"
                    :rules="doctorRules"
                    required
                  />
                </v-col>

                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newAppointment.patient"
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
                    label="Uwagi"
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
                <v-icon small class="mr-2" @click="editAppointment(item)">mdi-pencil</v-icon>
                <v-icon small @click="deleteAppointment(item)">mdi-delete</v-icon>
              </template>
            </v-data-table>
          </v-card>
        </v-window-item>

        <!-- Doctor Schedules -->
        <v-window-item value="schedule">
          <v-card flat class="pa-4 mt-4">
            <v-data-table
              :headers="doctorHeaders"
              :items="formattedDoctors"
              :items-per-page="5"
              class="elevation-1"
            >
              <template v-slot:item.schedule="{ item }">
                <v-btn small @click="editSchedule(item)">Edytuj grafik</v-btn>
              </template>
            </v-data-table>

            <v-dialog v-model="scheduleDialog" max-width="600">
              <v-card>
                <v-card-title>Edycja grafiku dla {{ selectedDoctor.fullName }}</v-card-title>
                <v-card-text>
                  <v-simple-table>
                    <thead>
                    <tr>
                      <th>Dzień</th>
                      <th>Godziny pracy</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr v-for="day in days" :key="day">
                      <td>{{ day }}</td>
                      <td>
                        <v-text-field
                          v-model="selectedDoctor.schedule[day]"
                          dense
                          :rules="scheduleRules"
                        />
                      </td>
                    </tr>
                    </tbody>
                  </v-simple-table>
                </v-card-text>
                <v-card-actions>
                  <v-spacer/>
                  <v-btn text @click="scheduleDialog = false">Anuluj</v-btn>
                  <v-btn text @click="saveSchedule">Zapisz</v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
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
import {it} from "vuetify/locale";

export default {
  data() {
    return {
      tab: 'add',
      timePicker: false,
      newAppointment: { doctor: null, patient: '', date: null, time: '', notes: '' },
      doctors: [],
      appointments: [],
      doctorRules: [v => !!v || 'Wybierz lekarza'],
      patientRules: [v => !!v || 'Wprowadź pacjenta'],
      dateRules: [
        v => !!v || 'Wpisz datę',
        v => /^([0-3]\d)\/(0[1-9]|1[0-2])\/\d{4}$/.test(v) || 'Format DD/MM/YYYY'
      ],
      timeRules: [
        v => !!v || 'Wybierz godzinę',
        v => /^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/.test(v) || 'Format HH:mm'
      ],
      scheduleRules: [
        v => !v || /^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(v) || 'Format HH:MM-HH:MM'
      ],
      days: ['Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek', 'Sobota', 'Niedziela'],
      scheduleDialog: false,
      selectedDoctor: { id: null, fullName: '', schedule: {} },
      snackbar: false,
      snackbarText: '',
      snackbarColor: 'success'
    }
  },
  computed: {
    appointmentHeaders() {
      return [
        { text: 'ID', value: 'id' },
        { text: 'Lekarz', value: 'doctorName' },
        { text: 'Pacjent', value: 'patient' },
        { text: 'Data i godzina', value: 'date' },
        { text: 'Opis', value: 'description' },
        { text: 'Akcje', value: 'actions', sortable: false }
      ]
    },
    doctorHeaders() {
      return [
        { text: 'Imię i nazwisko', value: 'fullName' },
        { text: 'Grafik', value: 'schedule', sortable: false }
      ]
    },
    formattedDoctors() {
      return this.doctors.map(d => ({
        ...d,
        fullName: `${d.firstName} ${d.lastName}`,
        schedule: this.formatSchedule(d.employeeWorkSchedules)
      }))
    },
    allowedMinutes() {
      return m => m % 15 === 0
    }
  },
  created() {
    this.loadDoctors()
    this.loadAppointments()
  },
  methods: {
    async loadDoctors() {
      try {
        const res = await doctorService.getDoctorsFullNames()
        this.doctors = res.data
      } catch {
        this.notify('Błąd ładowania lekarzy', 'error')
      }
    },
    async loadAppointments() {
      try {
        const res = await visitService.getVisits()
        this.appointments = res.data.map(item => ({
          ...item,
          date: this.formatDateTime(item.date)
        }))
      } catch {
        this.notify('Błąd ładowania wizyt', 'error')
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
      this.newAppointment = { doctor: null, patient: '', date: null, time: '', notes: '' }
      this.$refs.appointmentForm.resetValidation()
    },

    editAppointment(item) {
      this.notify(`Edycja: ${item.patient}`, 'info')
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
    }
  }
}

</script>

<style scoped>
.v-toolbar {
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
</style>
