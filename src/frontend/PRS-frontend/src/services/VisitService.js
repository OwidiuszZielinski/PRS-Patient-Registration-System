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
 },
 
 checkPaymentAndCreateVisit(visitId) {
   console.log('VisitService: Checking payment and creating visit for visitId:', visitId)
   return axios.post(`${BASE_URL}check-payment-and-create-visit/${visitId}`);
 }
}
