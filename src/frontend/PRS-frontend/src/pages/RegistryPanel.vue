<template>
  <v-app>
    <v-container>
      <!-- Nagłówek -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-toolbar color="#764ABC" dark flat>
            <v-toolbar-title>Panel rejestratorki</v-toolbar-title>
            <v-spacer/>
            <v-btn text @click="logout">Wyloguj</v-btn>
          </v-toolbar>
        </v-col>
      </v-row>

      <!-- Zakładki -->
      <v-tabs v-model="tab" grow>
        <v-tab value="add">Dodaj wizytę</v-tab>
        <v-tab value="list">Lista wizyt</v-tab>
        <v-tab value="schedule">Grafiki lekarzy</v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Dodawanie wizyty -->
        <v-window-item value="add">
          <v-card flat class="pa-4 mt-4">
            <v-form ref="appointmentForm" @submit.prevent="addAppointment">
              <v-row>
                <!-- Lekarz -->
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

                <!-- Gabinet -->
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.office"
                    :items="offices"
                    label="Gabinet"
                    :rules="officeRules"
                    required
                  />
                </v-col>

                <!-- Pacjent -->
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newAppointment.patient"
                    label="Pacjent"
                    :rules="patientRules"
                    required
                  />
                </v-col>

                <!-- Data wizyty -->
                <v-col cols="12" md="6">
                  <v-date-input
                    v-model="newAppointment.date"
                    label="Data wizyty"
                    :rules="dateRules"
                    placeholder="dd/mm/yyyy "
                    :display-format="dateFormat"
                    :first-day-of-week="1"
                    locale="pl"
                  />
                </v-col>

                <!-- Godzina wizyty -->
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

                <!-- Uwagi -->
                <v-col cols="12">
                  <v-textarea
                    v-model="newAppointment.notes"
                    label="Uwagi"
                  />
                </v-col>

                <!-- Akcje -->
                <v-col cols="12">
                  <v-btn type="submit" color="primary">Dodaj wizytę</v-btn>
                  <v-btn class="ml-2" @click="resetForm">Wyczyść</v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card>
        </v-window-item>

        <!-- Lista wizyt -->
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

        <!-- Grafik lekarzy -->
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
                      <tr><th>Dzień</th><th>Godziny pracy</th></tr>
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

      <!-- Snackbar -->
      <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" bottom>
        {{ snackbarText }}
      </v-snackbar>
    </v-container>
  </v-app>
</template>

<script>
import doctorService from '@/services/DoctorService'

export default {
  data() {
    return {
      tab: 'add',

      // pickery
      datePicker: false,
      timePicker: false,
      minDate: new Date().toISOString().slice(0,10),

      // formularz
      newAppointment: {
        doctor: null,
        office: null,
        patient: '',
        date: null,   // YYYY-MM-DD
        time: '',
        notes: ''
      },

      // dane
      doctors: [],
      appointments: [],

      // walidacja
      doctorRules: [v => !!v || 'Wybierz lekarza'],
      officeRules: [v => !!v || 'Wybierz gabinet'],
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
        v => !v || /^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(v)
              || 'Format HH:MM-HH:MM'
      ],

      // grafik
      days: ['Poniedziałek','Wtorek','Środa','Czwartek','Piątek','Sobota','Niedziela'],
      offices: ['Gabinet 1','Gabinet 2','Gabinet 3','Gabinet 4'],
      loadingDoctors: false,
      scheduleDialog: false,
      selectedDoctor: { id:null, fullName:'', licenseNumber:'', schedule:{} },

      // snackbar
      snackbar: false,
      snackbarText: '',
      snackbarColor: 'success'
    }
  },
  computed: {
    appointmentHeaders() {
      return [
        { text:'Lekarz', value:'doctorName' },
        { text:'Gabinet', value:'office' },
        { text:'Pacjent', value:'patient' },
        { text:'Data', value:'date' },
        { text:'Godzina', value:'time' },
        { text:'Uwagi', value:'notes' },
        { text:'Akcje', value:'actions', sortable:false }
      ]
    },
    doctorHeaders() {
      return [
        { text:'Imię i nazwisko', value:'fullName' },
        { text:'Numer licencji', value:'licenseNumber' },
        { text:'Grafik', value:'schedule', sortable:false }
      ]
    },
    formattedDoctors() {
      return this.doctors.map(d => ({
        ...d,
        fullName: `Dr ${d.firstName} ${d.lastName}`,
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
    dateFormat(date) {
        if (!date) return ''
        const day   = String(date.getDate()).padStart(2, '0');
        const month = String(date.getMonth() + 1).padStart(2, '0'); // miesiące liczone od 0
        const year  = date.getFullYear();

        return `${day}/${month}/${year}`
    },
    // formularz
    addAppointment() {
      if (!this.$refs.appointmentForm.validate()) {
        this.notify('Wypełnij wszystkie pola','error')
        return
      }
      if (!this.newAppointment.date) {
        this.notify('Wybierz datę','error')
        return
      }
      const doc = this.doctors.find(d=>d.id===this.newAppointment.doctor)
      if (!doc) {
        this.notify('Nie znaleziono lekarza','error')
        return
      }
      this.appointments.push({
        id: this.appointments.length+1,
        doctorName: doc.fullName,
        office: this.newAppointment.office,
        patient: this.newAppointment.patient,
        date: this.newAppointment.date,
        time: this.newAppointment.time,
        notes: this.newAppointment.notes
      })
      this.notify('Wizyta dodana!','success')
      this.resetForm()
    },
    resetForm() {
      this.newAppointment = { doctor:null, office:null, patient:'', date:null, time:'', notes:'' }
      this.$refs.appointmentForm.resetValidation()
    },
    // CRUD wizyt
    editAppointment(item) { this.notify(`Edycja: ${item.patient}`,'info') },
    deleteAppointment(item) {
      this.appointments = this.appointments.filter(a=>a.id!==item.id)
      this.notify('Wizyta usunięta','success')
    },
    // grafik
    editSchedule(d) {
      this.selectedDoctor = { id:d.id, fullName:d.fullName, licenseNumber:d.licenseNumber, schedule:{...d.schedule} }
      this.scheduleDialog = true
    },
    saveSchedule() {
      const idx = this.doctors.findIndex(d=>d.id===this.selectedDoctor.id)
      if (idx!==-1) this.doctors[idx].employeeWorkSchedules = this.convertScheduleToDto(this.selectedDoctor.schedule)
      this.scheduleDialog = false
      this.notify('Grafik zapisany','success')
    },
    convertScheduleToDto(schedule) {
      return Object.entries(schedule)
        .map(([day,h])=> h&&h.includes('-') ? { dayOfWeek:day, startTime:h.split('-')[0], endTime:h.split('-')[1] } : null)
        .filter(x=>x)
    },
    formatSchedule(ws) {
      const sched = {}
      this.days.forEach(day=> {
        const e = ws?.find(w=>w.dayOfWeek===day)
        sched[day] = e ? `${e.startTime}-${e.endTime}` : 'Nieczynne'
      })
      return sched
    },
    // dane
    async loadDoctors() {
      this.loadingDoctors = true
      try {
        const res = await doctorService.getDoctorsFullNames()
        this.doctors = res.data
      } catch {
        this.notify('Błąd ładowania lekarzy','error')
      } finally {
        this.loadingDoctors = false
      }
    },
    async loadAppointments() {
      this.appointments = [
        { id:1, doctorName:'Dr Jan Kowalski', office:'Gabinet 1', patient:'Adam Nowak', date:'2023-06-15', time:'10:00', notes:'Kontrolna' }
      ]
    },
    // pomocnicze
    notify(text,color) {
      this.snackbarText = text
      this.snackbarColor = color
      this.snackbar = true
    },
    logout() {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.v-toolbar { box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
</style>
