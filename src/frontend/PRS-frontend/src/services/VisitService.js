import axios from "axios";

const BASE_URL = "http://localhost:8080/api/visit/";

export default {
  addVisit(visitDto) {
    return axios.post(BASE_URL, visitDto);
  },
  getVisits() {
    return axios.get(BASE_URL)
  },
  delete(id) {
    return axios.delete(`${BASE_URL}${id}/`)
  },
  update(visitDto) {
   return axios.post(`${BASE_URL}update`, visitDto);
 }
}
