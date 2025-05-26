import axios from "axios";

const BASE_URL = "http://localhost:8080/api/patient";

export default {
  getPatients() {
    return axios.get(BASE_URL);
  },

  addPatient(patientDto) {
    return axios.post(BASE_URL, patientDto)
  },
  updatePatient(patientDto) {
    return axios.post(BASE_URL + "/update", patientDto)
  },
  delete(id) {
    return axios.delete(`${BASE_URL}/` + id)
  }
}
