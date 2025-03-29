import axios from "axios";

const BASE_URL = "http://localhost:8080/api";

export default {
  getFibo(number) {

    console.log("Fibo number:", number);  // Debugowanie, co jest przekazywane
    return axios.get(`${BASE_URL}/${number}`);
  }
}
