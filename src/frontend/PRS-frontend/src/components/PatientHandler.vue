<template>
  <v-card flat class="pa-4 mt-4">
    <!-- Edit Banner: show read-only fields when editing -->
    <v-alert
      v-if="isEditing"
      type="info"
      dense
      border="left"
      colored-border
      class="mb-4"
    >
      <div class="d-flex flex-column">
        <div><strong>First Name:</strong> {{ newPatient.firstname }}</div>
        <div><strong>PESEL:</strong> {{ newPatient.identificationNumber }}</div>
        <div><strong>Birth Date:</strong> {{ formattedBirthDate }}</div>
      </div>
    </v-alert>

    <v-form ref="patientForm" @submit.prevent="savePatient">
      <v-row>
        <!-- First Name and Last Name side by side -->
        <v-col cols="12" md="6">
          <v-text-field
            v-if="!isEditing"
            v-model="newPatient.firstname"
            label="First Name"
            :rules="nameRules"
            required
          />
          <v-text-field
            v-else
            v-model="newPatient.firstname"
            label="First Name"
            disabled
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-text-field
            v-model="newPatient.lastname"
            label="Last Name"
            :rules="nameRules"
            required
          />
        </v-col>

        <!-- PESEL and Birth Date when not editing -->
        <template v-if="!isEditing">
          <v-col cols="12" md="6">
            <v-text-field
              v-model="newPatient.identificationNumber"
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
        </template>

        <!-- Always show email and phone fields -->
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
          <v-btn type="submit" color="primary">
            {{ isEditing ? 'Update Patient' : 'Register Patient' }}
          </v-btn>
          <v-btn class="ml-2" @click="resetPatientForm">Clear</v-btn>
        </v-col>
      </v-row>
    </v-form>

    <!-- Data table remains unchanged -->
    <v-card flat class="pa-4 mt-6">
      <v-data-table
        :headers="patientHeaders"
        :items="patients"
        :items-per-page="10"
        class="elevation-1"
      >
        <template v-slot:item.actions="{ item }">
          <v-icon color="green" small class="mr-2" @click="startEdit(item)">mdi-pencil</v-icon>
          <v-icon color="red" small @click="deletePatient(item)">mdi-delete</v-icon>
        </template>
      </v-data-table>
    </v-card>
  </v-card>
</template>

<script>
import patientService from '@/services/PatientService'

export default {
  name: 'PatientHandler',
  props: {
    patients: {
      type: Array,
      required: true,
      default: () => []
    }
  },
  emits: ['patient-added', 'patient-updated', 'patient-deleted'],
  data() {
    return {
      newPatient: {
        id: null,
        firstname: '',
        lastname: '',
        identificationNumber: '',
        birthDate: null,
        email: '',
        phoneNumber: ''
      },
      editingIndex: null,
      patientHeaders: [
        { title: 'Patient ID', key: 'id', align: 'start', sortable: true },
        { title: 'First Name', key: 'firstname', align: 'start', sortable: true },
        { title: 'Last Name', key: 'lastname', align: 'start', sortable: true },
        { title: 'PESEL', key: 'identificationNumber', align: 'start', sortable: true },
        { title: 'Birth Date', key: 'birthDate', align: 'start', sortable: true },
        { title: 'Email', key: 'email', align: 'start', sortable: true },
        { title: 'Phone Number', key: 'phoneNumber', align: 'start', sortable: true },
        { title: 'Actions', key: 'actions', align: 'end', sortable: false }
      ],
      nameRules: [
        v => !!v || 'Name is required',
        v => (v && v.length >= 2) || 'Name must be at least 2 characters'
      ],
      peselRules: [
        v => !!v || 'PESEL is required',
        v => /^\d{11}$/.test(v) || 'PESEL must be 11 digits'
      ],
      dateRules: [
        v => !!v || 'Birth date is required',
        v => {
          const today = new Date()
          const date = new Date(v)
          return date < today || 'Birth date must be in the past'
        }
      ],
      emailRules: [
        v => !!v || 'E-mail is required',
        v => /.+@.+\..+/.test(v) || 'E-mail must be valid'
      ],
      phoneRules: [
        v => !!v || 'Phone number is required',
        v => /^[0-9+\-\s()]+$/.test(v) || 'Phone number must be valid'
      ],
      dateFormat: 'DD/MM/YYYY'
    }
  },
  computed: {
    isEditing() {
      return this.editingIndex !== null
    },
    formattedBirthDate() {
      if (!this.newPatient.birthDate) return ''
      const [year, month, day] = this.newPatient.birthDate.split('-')
      return `${day}/${month}/${year}`
    }
  },
  methods: {
    async savePatient() {
      if (!this.$refs.patientForm.validate()) return
      const payload = { ...this.newPatient }
      try {
        if (this.isEditing) {
          await patientService.updatePatient(payload)
          this.$emit('patient-updated')
        } else {
          await patientService.addPatient(payload)
          this.$emit('patient-added')
        }
        this.resetPatientForm()
      } catch (error) {
        const action = this.isEditing ? 'updating' : 'creating'
        this.notify(
          `Error ${action} patient: ${(error.response?.data?.message) || error.message}`,
          'error'
        )
      }
    },

    startEdit(item) {
      this.editingIndex = this.patients.indexOf(item)
      this.newPatient = { ...item }
    },

    async deletePatient(item) {
      try {
        await patientService.delete(item.id)
        this.$emit('patient-deleted')
      } catch (error) {
        console.error('Error deleting patient:', error)
        this.notify('Error deleting patient', 'error')
      }
    },

    resetPatientForm() {
      this.editingIndex = null
      this.newPatient = {
        id: null,
        firstname: '',
        lastname: '',
        identificationNumber: '',
        birthDate: null,
        email: '',
        phoneNumber: ''
      }
      this.$refs.patientForm.resetValidation()
    }
  }
}
</script>
