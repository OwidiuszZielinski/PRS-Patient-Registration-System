import axios from "axios";

const BASE_URL = "http://localhost:8080/api/payment/";
const VISIT_URL = "http://localhost:8080/api/visit/";

export default {
  createPayment(paymentRequest) {
    return axios.post(BASE_URL + "create", paymentRequest);
  },

  getPaymentStatus(paymentId) {
    return axios.get(BASE_URL + "status/" + paymentId);
  },

  addVisitWithPayment(visitDto) {
    return axios.post(VISIT_URL + "with-payment", visitDto);
  }
} 