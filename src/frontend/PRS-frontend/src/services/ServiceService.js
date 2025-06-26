import axios from 'axios'

const API_URL = 'http://localhost:8080/api/service/'

class ServiceService {
  getServices() {
    return axios.get(API_URL)
  }

  getService(id) {
    return axios.get(API_URL + id + '/')
  }
}

export default new ServiceService() 