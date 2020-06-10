import axios from 'axios'

const axios_instance = axios.create({
    baseURL: 'http://gateway:8080'
});

export default axios_instance;
