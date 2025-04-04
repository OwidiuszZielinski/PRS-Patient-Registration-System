import axios from "axios";

const BASE_URL = "http://localhost:8080/api/doctors/";

export default {
  getDoctors() {
    return axios.get(BASE_URL);
  }
}
