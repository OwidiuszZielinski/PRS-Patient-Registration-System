<template>
  <v-app>
    <v-container>
      <!-- Nagłówek -->
      <v-row class="mb-6">
        <v-col cols="12">
          <v-card flat>
            <v-toolbar color="#764ABC" dark>
              <v-toolbar-title>Panel rejestratorki</v-toolbar-title>
              <v-spacer></v-spacer>
              <v-btn @click="logout" text>Wyloguj</v-btn>
            </v-toolbar>
          </v-card>
        </v-col>
      </v-row>

      <!-- Zakładki -->
      <v-tabs v-model="tab" grow>
        <v-tab value="add">Dodaj wizytę</v-tab>
        <v-tab value="list">Lista wizyt</v-tab>
        <v-tab value="schedule">Grafiki lekarzy</v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Formularz dodawania wizyty -->
        <v-window-item value="add">
          <v-card flat class="pa-4 mt-4">
            <v-form @submit.prevent="addAppointment">
              <v-row>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.doctor"
                    :items="doctors"
                    :loading="loadingDoctors"
                    item-text="fullName"
                    item-value="id"
                    label="Lekarz"
                    :rules="[v => !!v || 'Wybierz lekarza']"
                    required
                  ></v-select>
                </v-col>
                <v-col cols="12" md="6">
                  <v-select
                    v-model="newAppointment.office"
                    :items="offices"
                    label="Gabinet"
                    :rules="[v => !!v || 'Wybierz gabinet']"
                    required
                  ></v-select>
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newAppointment.patient"
                    label="Pacjent"
                    :rules="[v => !!v || 'Wprowadź nazwisko pacjenta']"
                    required
                  ></v-text-field>
                </v-col>

          <v-col cols="12" md="6">
            <v-menu
              v-model="datePicker"
              :close-on-content-click="false"
              transition="scale-transition"
              offset-y
              min-width="auto"
            >
              <template v-slot:activator="{ on, attrs }">
                <v-text-field
                  v-model="displayDate"
                  label="Data wizyty (DD.MM.YYYY)"
                  prepend-icon="mdi-calendar"
                  v-bind="attrs"
                  v-on="on"
                  :rules="[
                    v => !!v || 'Wybierz datę wizyty',
                    v => /^(0[1-9]|[12][0-9]|3[01])\.(0[1-9]|1[0-2])\.\d{4}$/.test(v) || 'Data musi być w formacie DD.MM.YYYY'
                  ]"
                  required
                  mask="##.##.####"
                  hint="Wprowadź datę w formacie DD.MM.YYYY"
                  persistent-hint
                  @blur="parseInputDate"
                ></v-text-field>
              </template>
              <v-date-picker
                v-model="newAppointment.date"
                @input="updateDisplayDate"
                :min="minDate"
              ></v-date-picker>
            </v-menu>
          </v-col>
<v-col cols="12" md="6">
  <v-menu
    ref="timeMenu"
    v-model="timePicker"
    :close-on-content-click="false"
    :nudge-right="40"
    transition="scale-transition"
    offset-y
    max-width="290px"
    min-width="290px"
  >
    <template v-slot:activator="{ on, attrs }">
      <v-text-field
        v-model="newAppointment.time"
        label="Godzina"
        prepend-icon="mdi-clock-time-four-outline"
        v-bind="attrs"
        v-on="on"
        :rules="[
          v => !!v || 'Wybierz godzinę wizyty',
          v => /^(0[0-9]|1[0-9]|2[0-3]):([0-5][0-9])$/.test(v) || 'Godzina musi być w formacie HH:mm'
        ]"
        required
        mask="##:##"
        hint="Wprowadź godzinę w formacie HH:mm"
        persistent-hint
      ></v-text-field>
    </template>
    <v-time-picker
      v-if="timePicker"
      v-model="newAppointment.time"
      format="24hr"
      :allowed-minutes="allowedMinutes"
      @change="timePicker = false"
    ></v-time-picker>
  </v-menu>
</v-col>


                <v-col cols="12">
                  <v-textarea
                    v-model="newAppointment.notes"
                    label="Uwagi"
                  ></v-textarea>
                </v-col>
                <v-col cols="12">
                  <v-btn type="submit" color="primary">Dodaj wizytę</v-btn>
                  <v-btn @click="resetForm" class="ml-2">Wyczyść</v-btn>
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
                <v-icon small class="mr-2" @click="editAppointment(item)">
                  mdi-pencil
                </v-icon>
                <v-icon small @click="deleteAppointment(item)">
                  mdi-delete
                </v-icon>
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

            <!-- Dialog edycji grafiku -->
            <v-dialog v-model="scheduleDialog" max-width="600">
              <v-card>
                <v-card-title>Edycja grafiku dla {{ selectedDoctor.fullName }}</v-card-title>
                <v-card-text>
                  <v-simple-table>
                    <template v-slot:default>
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
                            :rules="[v => !v || /^([01]?[0-9]|2[0-3]):[0-5][0-9]-([01]?[0-9]|2[0-3]):[0-5][0-9]$/.test(v) || 'Format HH:MM-HH:MM']"
                          ></v-text-field>
                        </td>
                      </tr>
                      </tbody>
                    </template>
                  </v-simple-table>
                </v-card-text>
                <v-card-actions>
                  <v-spacer></v-spacer>
                  <v-btn color="blue darken-1" text @click="scheduleDialog = false">
                    Anuluj
                  </v-btn>
                  <v-btn color="blue darken-1" text @click="saveSchedule">
                    Zapisz
                  </v-btn>
                </v-card-actions>
              </v-card>
            </v-dialog>
          </v-card>
        </v-window-item>
      </v-window>
    </v-container>

  </v-app>
  <v-snackbar v-model="snackbar" :color="snackbarColor" timeout="3000" bottom>
    {{ snackbarText }}
  </v-snackbar>
</template>

<script>
import doctorService from '@/services/DoctorService';

export default {
  data() {
    return {
    snackbar: false, // Kontroluje wyświetlanie snakbara
        snackbarText: '', // Treść snakbara
        snackbarColor: 'success', // Kolor snakbara, domyślnie zielony
      tab: 'add',
      datePicker: false,
      timePicker: false,
      scheduleDialog: false,
      loadingDoctors: false,
       minDate: new Date().toISOString().substr(0, 10),
            displayDate: '', // Data w formacie widocznym dla użytkownika (DD.MM.YYYY),
      days: ['Poniedziałek', 'Wtorek', 'Środa', 'Czwartek', 'Piątek'],
      offices: ['Gabinet 1', 'Gabinet 2', 'Gabinet 3', 'Gabinet 4'],
      appointmentHeaders: [
        { text: 'Lekarz', value: 'doctorName' },
        { text: 'Gabinet', value: 'office' },
        { text: 'Pacjent', value: 'patient' },
        { text: 'Data', value: 'date' },
        { text: 'Godzina', value: 'time' },
        { text: 'Akcje', value: 'actions', sortable: false }
      ],
      doctorHeaders: [
        { text: 'Imię i nazwisko', value: 'fullName' },
        { text: 'Numer licencji', value: 'licenseNumber' },
        { text: 'Grafik', value: 'schedule', sortable: false }
      ],
      newAppointment: {
        doctor: null,
        office: null,
        patient: '',
        date: '',
        time: '',
        notes: ''
      },
      selectedDoctor: {
        id: null,
        fullName: '',
        licenseNumber: '',
        schedule: {}
      },
      doctors: [],
      appointments: [
        {
          id: 1,
          doctorId: 1,
          doctorName: 'Dr Jan Kowalski',
          office: 'Gabinet 1',
          patient: 'Adam Nowak',
          date: '2023-06-15',
          time: '10:00',
          notes: 'Kontrolna wizyta'
        }
      ]
    }
  },
  computed: {
    formattedDoctors() {
      return this.doctors.map(doctor => ({
        ...doctor,
        fullName: `Dr ${doctor.firstName} ${doctor.lastName}`,
        schedule: this.formatSchedule(doctor.employeeWorkSchedules)
      }));
    },

  },
  created() {
    this.loadDoctors();
  },
   methods: {
   showNotification(text, color) {
       this.snackbarText = text;
       this.snackbarColor = color;
       this.snackbar = true;
     },
      // Ładuje lekarzy
      async loadDoctors() {
        this.loadingDoctors = true;
        try {
          const response = await doctorService.getDoctors();
          this.doctors = response.data;
        } catch (error) {
          console.error('Błąd podczas ładowania lekarzy:', error);
          this.$toast.error('Nie udało się załadować listy lekarzy');
        } finally {
          this.loadingDoctors = false;
        }
      },

      // Formatuje grafik lekarza
      formatSchedule(workSchedules) {
        const schedule = {};
        this.days.forEach(day => {
          const daySchedule = workSchedules?.find(ws => ws.dayOfWeek === day);
          schedule[day] = daySchedule ? `${daySchedule.startTime}-${daySchedule.endTime}` : 'Nieczynne';
        });
        return schedule;
      },

      // Dodaje wizytę
     addAppointment() {
         if (!this.$refs.appointmentForm.validate()) {
           this.showNotification('Wypełnij poprawnie wszystkie wymagane pola', 'error');
           return;
         }

         // Sprawdź poprawność daty
         if (!this.isValidDate(this.newAppointment.date)) {
           this.showNotification('Wybierz poprawną datę wizyty', 'error');
           return;
         }

         const doctor = this.doctors.find(d => d.id === this.newAppointment.doctor);
         this.appointments.push({
           id: this.appointments.length + 1,
           doctorId: this.newAppointment.doctor,
           doctorName: `Dr ${doctor.firstName} ${doctor.lastName}`,
           office: this.newAppointment.office,
           patient: this.newAppointment.patient,
           date: this.newAppointment.date,
           time: this.newAppointment.time,
           notes: this.newAppointment.notes
         });

         this.showNotification('Wizyta została dodana!', 'success'); // Zielony snackbar
         this.resetForm();
       },

      // Edytuje wizytę
      editAppointment(item) {
        console.log('Edycja wizyty:', item);
      },

      // Usuwa wizytę
      deleteAppointment(item) {
        const index = this.appointments.findIndex(a => a.id === item.id);
        if (index !== -1) {
          this.appointments.splice(index, 1);
          this.$toast.success('Wizyta została usunięta');
        }
      },

      // Edytuje grafik lekarza
      editSchedule(doctor) {
        this.selectedDoctor = {
          id: doctor.id,
          fullName: doctor.fullName,
          licenseNumber: doctor.licenseNumber,
          schedule: { ...doctor.schedule }
        };
        this.scheduleDialog = true;
      },

      // Zapisuje zmiany grafiku
      async saveSchedule() {
        try {
          // Tutaj należy dodać logikę zapisu do backendu
          // np. await doctorService.updateSchedule(this.selectedDoctor.id, this.selectedDoctor.schedule);

          // Tymczasowa aktualizacja lokalna
          const index = this.doctors.findIndex(d => d.id === this.selectedDoctor.id);
          if (index !== -1) {
            this.doctors[index].employeeWorkSchedules = this.convertScheduleToDto(this.selectedDoctor.schedule);
          }

          this.scheduleDialog = false;
          this.$toast.success('Grafik został zaktualizowany');
        } catch (error) {
          console.error('Błąd podczas zapisywania grafiku:', error);
          this.$toast.error('Nie udało się zapisać zmian');
        }
      },

      // Konwertuje grafik na DTO
      convertScheduleToDto(schedule) {
        return Object.entries(schedule).map(([day, hours]) => {
          const [startTime, endTime] = hours.split('-');
          return { dayOfWeek: day, startTime, endTime };
        }).filter(ws => ws.startTime && ws.endTime);
      },

      // Resetuje formularz
      resetForm() {
        this.newAppointment = {
          doctor: null,
          office: null,
          patient: '',
          date: '',
          time: '',
          notes: ''
        };
        this.displayDate = '';
      },

      // Logowanie
      logout() {
        this.$router.push('/login');
      },
    },

    computed: {
      // Tylko pełne kwadranse
      allowedMinutes() {
        return (m) => m % 15 === 0;
      }
    }
  };
  </script>

  <style scoped>
  .v-toolbar {
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .v-text-field--error >>> .v-input__slot {
    border: 1px solid #ff5252 !important;
  }
  </style>
