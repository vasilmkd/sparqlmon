import axios from 'axios'

const axios_instance = axios.create({
    baseURL: 'http://localhost/api'
});

export default axios_instance;
