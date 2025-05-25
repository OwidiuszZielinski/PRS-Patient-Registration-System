import axios from "axios";

const BASE_URL = "http://localhost:8080/api/doctor/";

export default {
  getDoctors() {
    return axios.get(BASE_URL);
  },
  getDoctorsFullNames() {
    return axios.get(BASE_URL + "fullname");
  },
  addDoctor(doctorDto) {
    return axios.post(BASE_URL, doctorDto)
  },
  updateDoctor(doctorDto) {
    return axios.post(BASE_URL + "update", doctorDto)
  }
}
