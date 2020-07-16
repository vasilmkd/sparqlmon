import axios from 'axios'

const axios_instance = axios.create({
    baseURL: `http://${window.location.host}/api`
});

export default axios_instance;
