import axios from "axios";

const BASE_URL = "http://localhost:8080/api/payment/";
const VISIT_URL = "http://localhost:8080/api/visit/";

export default {
  // Tworzy płatność w PayU
  createPayment(paymentRequest) {
    return axios.post(BASE_URL + "create", paymentRequest);
  },

  // Pobiera status płatności
  getPaymentStatus(paymentId) {
    return axios.get(BASE_URL + "status/" + paymentId);
  },

  // Rejestruje wizytę
  addVisitWithPayment(visitDto) {
    return axios.post(VISIT_URL + "with-payment", visitDto);
  }
} 