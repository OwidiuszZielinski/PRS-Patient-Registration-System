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
        <v-tab value="patient">Patient</v-tab>
        <v-tab value="add">Add visit</v-tab>
        <v-tab value="list">Visits</v-tab>
        <v-tab value="doctor">Doctor</v-tab>
        <v-tab value="schedule">Schedules</v-tab>
      </v-tabs>

      <v-window v-model="tab">
        <!-- Patient Registration -->
        <v-window-item value="patient">
          <v-card flat class="pa-4 mt-4">
            <v-form ref="patientForm" @submit.prevent="addPatient">
              <v-row>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newPatient.firstName"
                    label="First Name"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newPatient.lastName"
                    label="Last Name"
                    :rules="nameRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newPatient.pesel"
                    label="PESEL"
                    :rules="peselRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-date-input
                    v-model="newPatient.birthDate"
                    label="Birth Date"
                    :rules="dateRules"
                    placeholder="dd/mm/yyyy"
                    :display-format="dateFormat"
                    :first-day-of-week="1"
                    locale="pl"
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newPatient.email"
                    label="Email"
                    :rules="emailRules"
                    required
                  />
                </v-col>
                <v-col cols="12" md="6">
                  <v-text-field
                    v-model="newPatient.phoneNumber"
                    label="Phone Number"
                    :rules="phoneRules"
                    required
                  />
                </v-col>
                <v-col cols="12">
                  <v-btn type="submit" color="primary">Register Patient</v-btn>
                  <v-btn class="ml-2" @click="resetPatientForm">Clear</v-btn>
                </v-col>
              </v-row>
            </v-form>
          </v-card>
        </v-window-item>

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
                <v-icon small class="mr-2" @click="editAppointment(item)">mdi-pencil</v-icon>
                <v-icon small @click="deleteAppointment(item)">mdi-delete</v-icon>
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
                <v-icon small class="mr-2" @click="editDoctor(item)">mdi-pencil</v-icon>
                <v-icon small @click="deleteDoctor(item)">mdi-delete</v-icon>
              </template>
                        </v-data-table>
                      </v-card>

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

export default {
  data() {
    return {
      tab: 'patient',
      timePicker: false,
      editTimePicker: false,
      newAppointment: { doctor: null, patient: '', date: null, time: '', notes: '' },
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
      doctors: [],
      doctorsToEdit: [],
      appointments: [
      ],
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
        v => v instanceof Date || 'Invalid date format'
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
      selectedDoctor: { id: null, fullName: '', schedule: {} },
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
      snackbarColor: 'success'
    }
  },
   computed: {
      appointmentHeaders() {
         return [
           { title: 'ID',           key: 'id',           align: 'start', sortable: true },
           { title: 'Doctor',       key: 'doctorName',   align: 'start', sortable: true },
           { title: 'Patient',      key: 'patient',      align: 'start', sortable: true },
           { title: 'Date',         key: 'date',         align: 'start', sortable: true },
           { title: 'Description',         key: 'description',  align: 'start', sortable: true },
           { title: 'Actions',        key: 'actions',      align: 'end',   sortable: false }
         ]
       },
       doctorHeaders() {
          return [
               { title: 'Doctor ID',      key: 'id',            align: 'start', sortable: true },
               { title: 'First Name',     key: 'firstName',     align: 'start', sortable: true },
               { title: 'Last Name',      key: 'lastName',      align: 'start', sortable: true },
               { title: 'License Number', key: 'licenseNumber', align: 'start', sortable: true },
               { title: 'Office ID',      key: 'officeId',      align: 'start', sortable: true },
               { title: 'Actions',        key: 'actions',      align: 'end',   sortable: false }
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
    this.loadDoctorsFullName()
    this.loadAppointments()
    this.loadDoctors()
  },
  methods: {
    async loadDoctorsFullName() {
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
        } catch (e) {
          this.notify('Błąd ładowania lekarzy', 'error')
        }
      },

    async addPatient() {
      try {
        // TODO: Uncomment when service is ready
        // const patientDto = {
        //   firstName: this.newPatient.firstName,
        //   lastName: this.newPatient.lastName,
        //   pesel: this.newPatient.pesel,
        //   birthDate: this.formatDateForBackend(this.newPatient.birthDate),
        //   email: this.newPatient.email,
        //   phoneNumber: this.newPatient.phoneNumber
        // };
        // await patientService.addPatient(patientDto);
        this.notify('Patient registered successfully!', 'success');
        this.resetPatientForm();
      } catch (error) {
        this.notify('Error registering patient: ' + (error.response?.data?.message || error.message), 'error');
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
        this.loadDoctorsFullName();
      } catch (error) {
        this.notify('Error registering doctor: ' + (error.response?.data?.message || error.message), 'error');
      }
    },
editDoctor(item) {
    this.editedDoctor = {
      firstName:     item.firstName,
      lastName:      item.lastName,
      licenseNumber: item.licenseNumber,
      officeId:      item.officeId
    };
    this.editDoctorDialog = true;
  },

  async updateDoctor() {
    try {
      const dto = {
        doctorId:     this.editedDoctor.id,
        firstName:    this.editedDoctor.firstName,
        lastName:     this.editedDoctor.lastName,
        licenseNumber:this.editedDoctor.licenseNumber,
        officeId:     this.editedDoctor.officeId
      };
      await doctorService.updateDoctor(dto);
      this.notify('Doctor updated successfully!', 'success');
      this.editDoctorDialog = false;
      this.loadDoctors();
    } catch (error) {
      this.notify('Error updating doctor: ' + (error.response?.data?.message || error.message), 'error');
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
      this.newAppointment = { doctor: null, patient: '', date: null, time: '', notes: '' }
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

    formatSchedule(schedules) {
      if (!schedules) return 'Brak danych'
      return schedules.map(s => `${s.day}: ${s.startTime}-${s.endTime}`).join(', ')
    },

    editSchedule(doctor) {
      this.selectedDoctor = {
        id: doctor.id,
        fullName: doctor.fullName,
        schedule: this.initializeSchedule(doctor)
      };
      this.scheduleDialog = true;
    },

    initializeSchedule(doctor) {
      const schedule = {};
      this.days.forEach(day => {
        schedule[day] = '';
      });
      // Można dodać logikę wypełniania istniejącym grafikiem
      return schedule;
    },

    async saveSchedule() {
      try {
        // Tutaj logika zapisywania grafiku
        this.notify('Grafik zaktualizowany', 'success');
        this.scheduleDialog = false;
      } catch (error) {
        this.notify('Błąd podczas zapisywania grafiku', 'error');
      }
    }
  }
}
</script>
