<template>
  <v-card flat class="pa-4 mt-4">
    <v-data-table
      :headers="doctorHeaders"
      :items="doctorsToEdit"
      :items-per-page="10"
      class="elevation-1"
    >
      <template v-slot:item.actions="{ item }">
        <v-btn color="primary" small class="ml-2" @click="editSchedule(item)">Edit Schedule</v-btn>
        <v-btn color="yellow" small class="ml-2" @click="viewSchedules(item)">show schedules</v-btn>
      </template>
    </v-data-table>

    <!-- Edit Schedule Dialog -->
    <v-dialog v-model="scheduleDialog" max-width="700">
      <v-card>
        <v-card-title>
          Edit schedule for {{ selectedDoctor.fullName }}
          <v-spacer />
          <v-btn icon small @click="addScheduleItem">
            <v-icon>mdi-plus</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-container fluid>
            <v-row
              v-for="(item, index) in selectedDoctor.scheduleItems"
              :key="index"
              class="align-center mb-4"
            >
              <v-col cols="12" md="4">
                <v-date-input
                  v-model="item.date"
                  label="Data"
                  :display-format="dateFormat"
                  :first-day-of-week="1"
                  locale="pl"
                  :rules="dateRules"
                  placeholder="dd/mm/yyyy"
                  dense
                  full-width
                />
              </v-col>
              <v-col cols="12" md="4">
                <v-menu
                  v-model="item.startMenu"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="auto"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                      v-bind="attrs"
                      v-on="on"
                      v-model="item.startTime"
                      label="Od"
                      prepend-icon="mdi-clock-time-four-outline"
                      :rules="timeRules"
                      hint="HH:mm"
                      persistent-hint
                      dense
                      full-width
                    />
                  </template>
                  <v-time-picker
                    v-model="item.startTime"
                    format="24hr"
                    :allowed-minutes="allowedMinutes"
                    @change="item.startMenu = false"
                  />
                </v-menu>
              </v-col>
              <v-col cols="12" md="4">
                <v-menu
                  v-model="item.endMenu"
                  :close-on-content-click="false"
                  transition="scale-transition"
                  offset-y
                  max-width="auto"
                >
                  <template v-slot:activator="{ on, attrs }">
                    <v-text-field
                      v-bind="attrs"
                      v-on="on"
                      v-model="item.endTime"
                      label="Do"
                      prepend-icon="mdi-clock-time-four-outline"
                      :rules="timeRules"
                      hint="HH:mm"
                      persistent-hint
                      dense
                      full-width
                    />
                  </template>
                  <v-time-picker
                    v-model="item.endTime"
                    format="24hr"
                    :allowed-minutes="allowedMinutes"
                    @change="item.endMenu = false"
                  />
                </v-menu>
              </v-col>
              <v-col cols="12" class="text-right">
                <v-btn icon small color="error" @click="removeScheduleItem(index)">
                  <v-icon>mdi-delete</v-icon>
                </v-btn>
              </v-col>
            </v-row>
          </v-container>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="scheduleDialog = false">Cancel</v-btn>
          <v-btn text @click="saveSchedule">Save</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <!-- View Schedules Dialog -->
    <v-dialog v-model="schedulesDialog" max-width="600">
      <v-card>
        <v-card-title>
          Schedules for {{ selectedDoctor.fullName }}
          <v-spacer />
          <v-btn icon small @click="schedulesDialog = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </v-card-title>
        <v-card-text>
          <v-data-table
            :headers="scheduleHeaders"
            :items="schedules"
            class="elevation-1"
          >
            <template v-slot:item.actions="{ item }">
              <v-btn icon small color="error" @click="deleteSchedule(item)">
                <v-icon>mdi-delete</v-icon>
              </v-btn>
            </template>
          </v-data-table>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn text @click="schedulesDialog = false">Close</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-card>
</template>

<script>
import doctorService from '@/services/DoctorService'
import axios from 'axios'

export default {
  props: {
    doctorsToEdit: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  data() {
    return {
      scheduleDialog: false,
      schedulesDialog: false,
      selectedDoctor: { id: null, fullName: '', scheduleItems: [] },
      schedules: [],
      dateRules: [
        v => !!v || 'Data jest wymagana'
      ],
      timeRules: [
        v => !!v || 'Godzina jest wymagana',
        v => /^([01]?\d|2[0-3]):[0-5]\d$/.test(v) || 'Format HH:mm'
      ]
    }
  },
  computed: {
    doctorHeaders() {
      return [
        { title: 'Doctor ID', key: 'id', align: 'start', sortable: true },
        { title: 'First Name', key: 'firstName', align: 'start', sortable: true },
        { title: 'Last Name', key: 'lastName', align: 'start', sortable: true },
        { title: 'License Number', key: 'licenseNumber', align: 'start', sortable: true },
        { title: 'Office ID', key: 'officeId', align: 'start', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false }
      ]
    },
    scheduleHeaders() {
      return [
        { title: 'Date', key: 'scheduleDate', align: 'start', sortable: true },
        { title: 'Start Time', key: 'startTime', align: 'start', sortable: true },
        { title: 'End Time', key: 'endTime', align: 'start', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false }
      ]
    },
    allowedMinutes() {
      return m => m % 15 === 0
    }
  },

  methods: {
    formatDateForBackend (date) {
      const d = new Date(date)
      const yyyy = d.getFullYear()
      const mm = String(d.getMonth() + 1).padStart(2, '0')
      const dd = String(d.getDate()).padStart(2, '0')
      return `${yyyy}-${mm}-${dd}`
    },
    editSchedule(doctor) {
      const scheduleItems = Array.isArray(doctor.scheduleItems)
        ? doctor.scheduleItems
        : []

      this.selectedDoctor = {
        id: doctor.id,
        fullName: doctor.fullName,
        scheduleItems: scheduleItems.map(item => ({
          ...item,
          startMenu: false,
          endMenu: false
        }))
      }
      this.scheduleDialog = true
    },
    async viewSchedules(doctor) {
      try {
        const resp = await axios.get(`http://localhost:8080/api/schedule/${doctor.id}`)
        this.schedules = resp.data
        this.selectedDoctor = { id: doctor.id, fullName: doctor.fullName }
        this.schedulesDialog = true
      } catch (error) {
        console.error(error)
        this.$emit('notify', { text: 'Błąd ładowania grafików', color: 'error' })
      }
    },
    async deleteSchedule(item) {
      try {
        const url = `http://localhost:8080/api/schedule/${this.selectedDoctor.id}`
        await axios.delete(url, {
          data: { scheduleDate: item.scheduleDate }
        });
        this.$emit('notify', { text: 'Grafik usunięty', color: 'success' })
        this.viewSchedules({ id: this.selectedDoctor.id, fullName: this.selectedDoctor.fullName })
      } catch (error) {
        console.error(error)
        this.$emit('notify', { text: 'Błąd podczas usuwania grafiku', color: 'error' })
      }
    },
    addScheduleItem() {
      this.selectedDoctor.scheduleItems.push({
        date: null,
        startTime: '',
        endTime: '',
        startMenu: false,
        endMenu: false
      })
    },
    removeScheduleItem(index) {
      this.selectedDoctor.scheduleItems.splice(index, 1)
    },
    dateFormat(date) {
      if (!date) return ''
      const d = new Date(date)
      const day = String(d.getDate()).padStart(2, '0')
      const month = String(d.getMonth() + 1).padStart(2, '0')
      const year = d.getFullYear()
      return `${day}/${month}/${year}`
    },
    async saveSchedule () {
      const payload = this.selectedDoctor.scheduleItems.map(item => {
        const start = item.startTime.length === 4 ? `0${item.startTime}` : item.startTime
        const end = item.endTime.length === 4 ? `0${item.endTime}` : item.endTime
        return {
          scheduleDate: this.formatDateForBackend(item.date),
          startTime: start,
          endTime: end
        }
      })

      try {
        const url = `http://localhost:8080/api/schedule/${this.selectedDoctor.id}`
        await axios.post(url, payload)
        this.$emit('notify', { text: 'Grafik zaktualizowany', color: 'success' })
        this.scheduleDialog = false
      } catch (error) {
        console.error(error)
        this.$emit('notify', { text: 'Błąd podczas zapisywania grafiku', color: 'error' })
      }
    }
  }
}
</script>
